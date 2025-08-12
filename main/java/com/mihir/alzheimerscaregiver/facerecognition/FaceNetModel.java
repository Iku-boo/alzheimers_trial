package com.mihir.alzheimerscaregiver.facerecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FaceNetModel {
    private static final String TAG = "FaceNetModel";
    private static final String MODEL_FILE = "mobile_face_net.tflite";
    private static final int INPUT_SIZE = 112;
    private static final int EMBEDDING_SIZE = 192;

    private Interpreter interpreter;
    private ByteBuffer inputBuffer;
    private FloatBuffer outputBuffer;

    public FaceNetModel(Context context) {
        try {
            ByteBuffer model = FileUtil.loadMappedFile(context, MODEL_FILE);
            interpreter = new Interpreter(model);

            // Initialize buffers
            inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
            inputBuffer.order(ByteOrder.nativeOrder());

            outputBuffer = ByteBuffer.allocateDirect(4 * EMBEDDING_SIZE).asFloatBuffer();
            outputBuffer.order();

        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
        }
    }

    public float[] getFaceEmbedding(Bitmap bitmap) {
        if (interpreter == null) {
            Log.e(TAG, "Model not loaded");
            return null;
        }

        // Preprocess image
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);
        convertBitmapToByteBuffer(resizedBitmap);

        // Run inference
        outputBuffer.rewind();
        interpreter.run(inputBuffer, outputBuffer);

        // Get output
        float[] embedding = new float[EMBEDDING_SIZE];
        outputBuffer.rewind();
        outputBuffer.get(embedding);

        return embedding;
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (inputBuffer == null) {
            return;
        }

        inputBuffer.rewind();

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];

                // Normalize pixel values to [-1, 1]
                inputBuffer.putFloat(((val >> 16) & 0xFF) / 127.5f - 1.0f);
                inputBuffer.putFloat(((val >> 8) & 0xFF) / 127.5f - 1.0f);
                inputBuffer.putFloat((val & 0xFF) / 127.5f - 1.0f);
            }
        }
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }

    // Calculate cosine similarity between two embeddings
    public static float calculateSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            return 0f;
        }

        float dotProduct = 0f;
        float norm1 = 0f;
        float norm2 = 0f;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }

        norm1 = (float) Math.sqrt(norm1);
        norm2 = (float) Math.sqrt(norm2);

        if (norm1 == 0f || norm2 == 0f) {
            return 0f;
        }

        return dotProduct / (norm1 * norm2);
    }
}