package com.mihir.alzheimerscaregiver.facerecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.mihir.alzheimerscaregiver.R;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceRecognitionActivity extends AppCompatActivity {
    private static final String TAG = "FaceRecognitionActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    private PreviewView previewView;
    private Button btnAddFace, btnRecognize, btnBack;
    private TextView tvResult;

    private ProcessCameraProvider cameraProvider;
    private FaceDetector faceDetector;
    private FaceRecognitionHelper faceRecognitionHelper;
    private ExecutorService cameraExecutor;

    private boolean isAddingFace = false;
    private boolean isRecognizing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        initViews();
        initFaceDetection();
        setupClickListeners();

        faceRecognitionHelper = new FaceRecognitionHelper(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initViews() {
        previewView = findViewById(R.id.previewView);
        btnAddFace = findViewById(R.id.btnAddFace);
        btnRecognize = findViewById(R.id.btnRecognize);
        btnBack = findViewById(R.id.btnBack);
        tvResult = findViewById(R.id.tvResult);
    }

    private void initFaceDetection() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setMinFaceSize(0.1f)
                .build();

        faceDetector = FaceDetection.getClient(options);
    }

    private void setupClickListeners() {
        btnAddFace.setOnClickListener(v -> {
            showAddFaceDialog();
        });

        btnRecognize.setOnClickListener(v -> {
            isRecognizing = true;
            isAddingFace = false;
            tvResult.setText("Recognizing face...");
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void showAddFaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Face");

        final EditText input = new EditText(this);
        input.setHint("Enter person's name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                isAddingFace = true;
                isRecognizing = false;
                tvResult.setText("Position face in camera to add: " + name);
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;

        // Preview
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image analysis for face detection
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeFace);

        // Select front camera
        CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera use cases", e);
        }
    }

    private void analyzeFace(ImageProxy imageProxy) {
        @SuppressWarnings("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            faceDetector.process(image)
                    .addOnSuccessListener(faces -> processFaces(faces, imageProxy))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Face detection failed", e);
                        imageProxy.close();
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void processFaces(List<Face> faces, ImageProxy imageProxy) {
        if (faces.isEmpty()) {
            runOnUiThread(() -> {
                if (isAddingFace || isRecognizing) {
                    tvResult.setText("No face detected. Please position your face in the camera.");
                }
            });
            return;
        }

        // Get the first face
        Face face = faces.get(0);
        Bitmap bitmap = imageProxyToBitmap(imageProxy);

        if (bitmap != null) {
            // Crop face from bitmap
            Rect bounds = face.getBoundingBox();
            Bitmap faceBitmap = cropFace(bitmap, bounds, imageProxy.getWidth(), imageProxy.getHeight());

            if (isAddingFace && faceBitmap != null) {
                handleAddFace(faceBitmap);
            } else if (isRecognizing && faceBitmap != null) {
                handleRecognizeFace(faceBitmap);
            }
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        try {
            @SuppressWarnings("UnsafeOptInUsageError")
            Image image = imageProxy.getImage();
            if (image == null) return null;

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Error converting ImageProxy to Bitmap", e);
            return null;
        }
    }

    private Bitmap cropFace(Bitmap bitmap, Rect bounds, int imageWidth, int imageHeight) {
        try {
            // Scale bounds to bitmap dimensions
            float scaleX = (float) bitmap.getWidth() / imageWidth;
            float scaleY = (float) bitmap.getHeight() / imageHeight;

            int left = Math.max(0, (int) (bounds.left * scaleX));
            int top = Math.max(0, (int) (bounds.top * scaleY));
            int right = Math.min(bitmap.getWidth(), (int) (bounds.right * scaleX));
            int bottom = Math.min(bitmap.getHeight(), (int) (bounds.bottom * scaleY));

            int width = right - left;
            int height = bottom - top;

            if (width > 0 && height > 0) {
                return Bitmap.createBitmap(bitmap, left, top, width, height);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cropping face", e);
        }
        return null;
    }

    private void handleAddFace(Bitmap faceBitmap) {
        // Get the name from the dialog (you'll need to store this)
        // For now, we'll use a placeholder
        String personName = getCurrentPersonName(); // Implement this method

        if (personName != null && !personName.isEmpty()) {
            boolean success = faceRecognitionHelper.registerFace(personName, faceBitmap);

            runOnUiThread(() -> {
                if (success) {
                    tvResult.setText("Face added successfully for " + personName);
                    Toast.makeText(this, "Face registered for " + personName, Toast.LENGTH_SHORT).show();
                } else {
                    tvResult.setText("Failed to add face. Please try again.");
                }
                isAddingFace = false;
            });
        }
    }

    private void handleRecognizeFace(Bitmap faceBitmap) {
        FaceRecognitionHelper.RecognitionResult result = faceRecognitionHelper.recognizeFace(faceBitmap);

        runOnUiThread(() -> {
            tvResult.setText(result.toString());
            isRecognizing = false;
        });
    }

    private String currentPersonName = "";

    private String getCurrentPersonName() {
        return currentPersonName;
    }

    // Update the showAddFaceDialog method to store the name
    private void showAddFaceDialogUpdated() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Face");

        final EditText input = new EditText(this);
        input.setHint("Enter person's name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                currentPersonName = name;
                isAddingFace = true;
                isRecognizing = false;
                tvResult.setText("Position face in camera to add: " + name);
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (faceRecognitionHelper != null) {
            faceRecognitionHelper.close();
        }
    }
}