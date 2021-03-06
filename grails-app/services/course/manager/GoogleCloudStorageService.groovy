package course.manager

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.*
import grails.transaction.Transactional
import org.springframework.beans.factory.InitializingBean

import java.util.concurrent.TimeUnit

@Transactional
class GoogleCloudStorageService implements InitializingBean {

    def grailsApplication
    Storage storage
    String bucket
    Boolean ready = false

    void afterPropertiesSet() throws Exception {
        try {
            storage = StorageOptions.newBuilder()
                    .setProjectId(grailsApplication.config.getRequiredProperty('google.cloud.storage.projectId'))
                    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(grailsApplication.config.getRequiredProperty('google.cloud.storage.credentialsFile'))))
                    .build()
                    .getService()
            bucket = grailsApplication.config.getRequiredProperty('google.cloud.storage.bucket')
            ready = true
        } catch (all) {
            log.warn("Error when making GCS Setup: $all")
            ready = false
        }
    }

    def listBucketsAndObjects() {
        log.info("My buckets:")
        for (Bucket bucket : storage.list().iterateAll()) {
            log.info(bucket.toString())

            // List all blobs in the bucket
            log.info("Blobs in the bucket:")
            for (Blob blob : bucket.list().iterateAll()) {
                log.info(blob.toString())
                log.info(blob.signUrl(14, TimeUnit.DAYS).toString())
            }
        }
    }

    def getUrlForObject(String blobName, Long daysDuration = 3650) {

        if (!ready) {
            return ""
        } else {
            BlobId blobId = BlobId.of(bucket, blobName);
            Blob blob = storage.get(blobId);
            if (blob) {
                return blob.signUrl(daysDuration, TimeUnit.DAYS).toString()
            } else {
                log.warn("Can't get URL for Blob: $blobName")
                return ""
            }
        }
    }

    def putObject(String objectUrl, byte[] obj, String contentType) {
        log.info("Creating new Blob on $objectUrl")
        BlobId blobId = BlobId.of(bucket, objectUrl)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
        return storage.create(blobInfo, obj)
    }

    def removeObject(String objectUrl) {
        BlobId blobId = BlobId.of(bucket, objectUrl ?: "")
        boolean deleted = storage.delete(blobId)
        return deleted
    }

    def replaceObject(String objectUrl, byte[] obj, String contentType) {
        def deleteResult = removeObject(objectUrl)
        log.info("Deleted $objectUrl? $deleteResult")

        return putObject(objectUrl, obj, contentType)
    }

    def existsObject() {

    }

}
