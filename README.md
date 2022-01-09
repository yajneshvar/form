
add Google cloud default credentials with: https://cloud.google.com/sdk/gcloud/reference/iam/service-accounts/keys/create

ensure domain delegation is setup for the Google workspace: https://developers.google.com/admin-sdk/directory/v1/guides/delegation#delegate_domain-wide_authority_to_your_service_account

### Command to run application
GOOGLE_APPLICATION_CREDENTIALS=gcloud-credential.json ./gradlew run

### Command to upload to registry 
./gradlew jib

### Update cloud run
gcloud run deploy \
    --image=gcr.io/forms-304923/forms-1.0:latest \
    --platform managed \
    --allow-unauthenticated
