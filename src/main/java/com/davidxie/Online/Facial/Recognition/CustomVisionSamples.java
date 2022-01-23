/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 */

package com.davidxie.Online.Facial.Recognition;

// <snippet_imports>
import java.util.Collections;
import java.util.UUID;

import com.google.common.io.ByteStreams;

import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.ImageFileCreateBatch;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.ImageFileCreateEntry;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Iteration;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Project;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Region;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.TrainProjectOptionalParameter;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingClient;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.Trainings;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.ImagePrediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.Prediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionClient;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Tag;
// </snippet_imports>

public class CustomVisionSamples {

    // <snippet_creds>
    final static String trainingApiKey = APIKey.getTrainingApiKey();
    final static String trainingEndpoint = APIKey.getTrainingEndpoint();
    final static String predictionApiKey = APIKey.getPredictionApiKey();
    final static String predictionEndpoint = APIKey.getPredictionEndpoint();
    final static String predictionResourceId = APIKey.getPredictionResourceId();
    // </snippet_creds>

    static Tag hemlockTag = null;
    static Tag cherryTag = null;
    static Iteration iteration = null;

    static String publishedModelName = "myTestVisionModel";

    /**
     * Main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) throws InterruptedException {

        // <snippet_auth>
        // Authenticate
        CustomVisionTrainingClient trainClient = CustomVisionTrainingManager
                .authenticate(trainingEndpoint, trainingApiKey)
                .withEndpoint(trainingEndpoint);
        CustomVisionPredictionClient predictor = CustomVisionPredictionManager
                .authenticate(predictionEndpoint, predictionApiKey)
                .withEndpoint(predictionEndpoint);
        // </snippet_auth>

        // <snippet_maincalls> for image classification
        Project project = createProject(trainClient);
        addTags(trainClient, project);
        uploadImages(trainClient, project);
        trainProject(trainClient, project);
        publishIteration(trainClient, project);
        testProject(predictor, project);
        // </snippet_maincalls>

    }

    // IMAGE CLASSIFICATION
    //

    // <snippet_create>
    public static Project createProject(CustomVisionTrainingClient trainClient) {
        System.out.println("ImageClassification Sample");
        Trainings trainer = trainClient.trainings();

        System.out.println("Creating project...");
        Project project = trainer.createProject().withName("Sample Java Project").execute();

        return project;
    }
    // </snippet_create>

    // <snippet_tags>
    public static void addTags(CustomVisionTrainingClient trainClient, Project project) {

        Trainings trainer = trainClient.trainings();

        // create hemlock tag
        hemlockTag = trainer.createTag().withProjectId(project.id()).withName("Hemlock").execute();
        // create cherry tag
        cherryTag = trainer.createTag().withProjectId(project.id()).withName("Japanese Cherry").execute();
    }
    // </snippet_tags>

    // <snippet_upload>
    public static void uploadImages(CustomVisionTrainingClient trainClient, Project project) {
        Trainings trainer = trainClient.trainings();
        System.out.println("Adding images...");
        for (int i = 1; i <= 10; i++) {
            String fileName = "hemlock_" + i + ".jpg";
            byte[] contents = GetImage("/Hemlock", fileName);
            AddImageToProject(trainer, project, fileName, contents, hemlockTag.id(), null);
        }

        for (int i = 1; i <= 10; i++) {
            String fileName = "japanese_cherry_" + i + ".jpg";
            byte[] contents = GetImage("/Japanese_Cherry", fileName);
            AddImageToProject(trainer, project, fileName, contents, cherryTag.id(), null);
        }
    }
    // </snippet_upload>

    // <snippet_train>
    public static void trainProject(CustomVisionTrainingClient trainClient, Project project) throws InterruptedException {
        System.out.println("Training...");
        Trainings trainer = trainClient.trainings();

        iteration = trainer.trainProject(project.id(), new TrainProjectOptionalParameter());

        while (iteration.status().equals("Training")) {
            System.out.println("Training Status: " + iteration.status());
            Thread.sleep(1000);
            iteration = trainer.getIteration(project.id(), iteration.id());
        }
        System.out.println("Training Status: " + iteration.status());
    }
    // </snippet_train>

    // <snippet_publish>
    public static String publishIteration(CustomVisionTrainingClient trainClient, Project project) {
        Trainings trainer = trainClient.trainings();
        // The iteration is now trained. Publish it to the prediction endpoint.

        trainer.publishIteration(project.id(), iteration.id(), publishedModelName, predictionResourceId);

        return "finished publishIteration";
    }
    // </snippet_publish>

    // use below for url
    // String url = "some url";
    // ImagePrediction results = predictor.predictions().classifyImageUrl()
    // .withProjectId(project.id())
    // .withPublishedName(publishedModelName)
    // .withUrl(url)
    // .execute();

    // <snippet_predict>
    // load test image
    public static void testProject(CustomVisionPredictionClient predictor, Project project) {

        byte[] testImage = GetImage("/Test", "test_image.jpg");

        // predict
        ImagePrediction results = predictor.predictions().classifyImage().withProjectId(project.id())
                .withPublishedName(publishedModelName).withImageData(testImage).execute();

        for (Prediction prediction : results.predictions()) {
            System.out.println(String.format("\t%s: %.2f%%", prediction.tagName(), prediction.probability() * 100.0f));
        }
    }
    // </snippet_predict>

    // <snippet_helpers>
    private static void AddImageToProject(Trainings trainer, Project project, String fileName, byte[] contents,
                                          UUID tag, double[] regionValues) {
        System.out.println("Adding image: " + fileName);
        ImageFileCreateEntry file = new ImageFileCreateEntry().withName(fileName).withContents(contents);

        ImageFileCreateBatch batch = new ImageFileCreateBatch().withImages(Collections.singletonList(file));

        // If Optional region is specified, tack it on and place the tag there,
        // otherwise
        // add it to the batch.
        if (regionValues != null) {
            Region region = new Region().withTagId(tag).withLeft(regionValues[0]).withTop(regionValues[1])
                    .withWidth(regionValues[2]).withHeight(regionValues[3]);
            file = file.withRegions(Collections.singletonList(region));
        } else {
            batch = batch.withTagIds(Collections.singletonList(tag));
        }

        trainer.createImagesFromFiles(project.id(), batch);
    }

    private static byte[] GetImage(String folder, String fileName) {
        try {
            return ByteStreams.toByteArray(CustomVisionSamples.class.getResourceAsStream(folder + "/" + fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    // </snippet_helpers>
}