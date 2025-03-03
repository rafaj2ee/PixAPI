package com.rafaj2ee.service;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacpp.indexer.IntIndexer; // Import necessário
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

@Service
public class BiometricService {

    private final CascadeClassifier faceDetector;
    private final FaceRecognizer faceRecognizer;
    private static final double CONFIDENCE_THRESHOLD = 60.0;

    public BiometricService() {
        this.faceDetector = loadCascade();
        this.faceRecognizer = LBPHFaceRecognizer.create();
    }

    private CascadeClassifier loadCascade() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/haarcascade_frontalface_alt.xml");
            File tempFile = File.createTempFile("haarcascade", ".xml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return new CascadeClassifier(tempFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar classificador", e);
        }
    }

    public boolean compararFaces(byte[] imagem1, byte[] imagem2) {
        Mat face1 = preprocessarFace(imagem1);
        Mat face2 = preprocessarFace(imagem2);
        
        if (face1 == null || face2 == null) return false;
        
        // Criar containers para treinamento
        MatVector images = new MatVector(2);
        images.put(0, face1);
        images.put(1, face2);
        
        // Criar matriz de labels usando IntIndexer (CORREÇÃO)
        Mat labels = new Mat(2, 1, CV_32SC1);
        IntIndexer labelIndexer = labels.createIndexer(); // Criar indexador para inteiros
        labelIndexer.put(0, 0, 0); // Linha 0, Coluna 0 → Label 0
        labelIndexer.put(1, 0, 1); // Linha 1, Coluna 0 → Label 1

        // Treinar o modelo
        faceRecognizer.train(images, labels);
        
        // Prever similaridade
        int[] predictedLabel = new int[1];
        double[] confidence = new double[1];
        faceRecognizer.predict(face2, predictedLabel, confidence);
        
        return confidence[0] < CONFIDENCE_THRESHOLD;
    }

    private Mat preprocessarFace(byte[] imagemBytes) {
        try {
            Mat mat = opencv_imgcodecs.imdecode(new Mat(imagemBytes), IMREAD_GRAYSCALE);
            
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(mat, faces);
            
            if (faces.size() == 0) return null;
            
            Rect faceRect = faces.get(0);
            Mat face = new Mat(mat, faceRect);
            
            // Pré-processamento
            resize(face, face, new Size(256, 256));
            opencv_imgproc.equalizeHist(face, face);
            
            return face;
        } catch (Exception e) {
            throw new RuntimeException("Erro no pré-processamento", e);
        }
    }
}