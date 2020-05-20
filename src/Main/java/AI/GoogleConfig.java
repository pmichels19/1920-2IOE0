/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package AI;

// [START automl_vision_classification_predict]
import com.google.cloud.automl.v1.AnnotationPayload;
import com.google.cloud.automl.v1.ExamplePayload;
import com.google.cloud.automl.v1.Image;
import com.google.cloud.automl.v1.ModelName;
import com.google.cloud.automl.v1.PredictRequest;
import com.google.cloud.automl.v1.PredictResponse;
import com.google.cloud.automl.v1.PredictionServiceClient;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class GoogleConfig {

     final String PROJECTID = "dbl-2ioe0-276912";
     final String MODELID = "ICN6912598817502134272";

     /** Classifies the image stored in the sent path.
      *
      * @param filePath path of the stored image to classify
      * @throws IOException
      */
     String[] predict(String filePath) throws IOException {
          // Initialize client that will be used to send requests. This client only needs to be created
          // once, and can be reused for multiple requests. After completing all of your requests, call
          // the "close" method on the client to safely clean up any remaining background resources.
          try (PredictionServiceClient client = PredictionServiceClient.create()) {
               // Get the full path of the model.
               ModelName name = ModelName.of(this.PROJECTID, "us-central1", this.MODELID);
               ByteString content = ByteString.copyFrom(Files.readAllBytes(Paths.get(filePath)));
               Image image = Image.newBuilder().setImageBytes(content).build();
               ExamplePayload payload = ExamplePayload.newBuilder().setImage(image).build();
               PredictRequest predictRequest =
                       PredictRequest.newBuilder()
                               .setName(name.toString())
                               .setPayload(payload)
                               .putParams(
                                       "score_threshold", "0.4") // [0.0-1.0] Only produce results higher than this value
                               .build();

               PredictResponse response = client.predict(predictRequest);

               String[] tempData = new String[2];

               for (AnnotationPayload annotationPayload : response.getPayloadList()) {
                    tempData[0] = annotationPayload.getDisplayName();
                    tempData[1] = Float.toString(annotationPayload.getClassification().getScore());

                    System.out.format("Predicted class name: %s\n", annotationPayload.getDisplayName());
                    System.out.format(
                            "Predicted class score: %.2f\n", annotationPayload.getClassification().getScore());
               }

               return tempData;
          }
     }
}
// [END automl_vision_classification_predict]