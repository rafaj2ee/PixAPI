terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.84.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.17.0"

  name = "minha-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["us-east-1a", "us-east-1b", "us-east-1c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]
  enable_nat_gateway = true

  private_subnet_tags = {
    Name                                    = format("%s-sub-private", var.eks_name),
    "kubernetes.io/role/internal-elb"       = 1,
    "kubernetes.io/cluster/${var.eks_name}" = "shared"
  }

  public_subnet_tags = {
    Name                                    = format("%s-sub-public", var.eks_name),
    "kubernetes.io/role/elb"                = 1,
    "kubernetes.io/cluster/${var.eks_name}" = "shared"
  }
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "20.33.0"

  cluster_name                             = var.eks_name
  cluster_version                          = var.eks_version
  subnet_ids                               = module.vpc.private_subnets
  vpc_id                                   = module.vpc.vpc_id
  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true

  eks_managed_node_groups = {
    live = {
      min_size     = 1
      max_size     = 1
      desired_size = 1

      instance_types = ["t2.micro"]
    }
  }
}

resource "aws_security_group" "ec2_sg" {
  vpc_id = module.vpc.vpc_id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Allow SSH from anywhere (restrict in production)
  }

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Allow access to port 9090 from anywhere
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]  # Allow all outbound traffic
  }

  tags = {
    Name = "ec2-sg"
  }
}

resource "aws_instance" "public_ec2" {
  ami                         = "ami-0c02fb55956c7d316"  # Amazon Linux 2 AMI (us-east-1)
  instance_type               = "t2.micro"
  subnet_id                   = module.vpc.public_subnets[0]  # Use the first public subnet
  associate_public_ip_address = true  # Assign a public IP
  vpc_security_group_ids      = [aws_security_group.ec2_sg.id]
  key_name                    = "live"  # Use the existing key pair named "live"

  tags = {
    Name = "public-ec2-instance"
  }
}

output "ec2_public_ip" {
  value = aws_instance.public_ec2.public_ip
}

variable "eks_name" {
  default = "terraform-eks"
}

variable "eks_version" {
  default = "1.31"
}
