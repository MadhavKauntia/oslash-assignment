# OSlash Assignment

## Problem Statement
https://www.notion.so/OSlash-Integration-Engineering-Assignment-Java-9daac3450d0a4cbc9ed5d024caba2080

## Demo Video
https://www.loom.com/share/52adcfc539a1467b87e7d28deabb7cc5

## Approach Taken

### Assumptions

- The Google Drive folder ID will be provided by the user
- The updates don't have to be instantaneous, i.e, if a user uploads a file in the drive, it doesn't immediately reflect in the json or the local folder. Some delay is acceptable as Google Drive does not provide any API to listen to the changes of a folder.

### Backend

- When the script starts, it initially pulls all files stored in the folder. It writes them to the local folder if number of files is greater than the specified threshold.
- Once the existing files are written, a cron job starts.
- This cron job runs periodically (can be configured by the user through environment variable `periodic.check.time.in.mins`) and checks if there are any new files in the folder.
- If a new file is found, it adds the metadata of the file to the heap in an `ArrayList`.
- When the number of events in the metadata list becomes equal to the threshold, these files are downloaded to a local folder and the metadata is updated in the same folder in `events.json`.
- Downloading of the batch of files is done concurrently. The batch size or the maximum concurrency can be configured by the user through an environment variable `max.concurrency.batch.size`.
- Since internal clock of a system can be unreliable at times, the script makes sure duplicate files are not downloaded or written into the `events.json` file by maintaning a `HashSet` of already processed fileIds.

![Architecture](/assets/architecture.jpg)

### Known Improvements / Future Scope

- The cron job needs to use a locking library like `shedlock` if it is distributed.
- If script crashes or if the instance restarts, the script should ideally resume from last checkpoint.
- Currently, the script only supports public folders since it is using an API key. Private folders can be integrated by using Google Auth.

## Results

## Steps to Run

- Clone the repo
- Add/update the following properties in `application.json`
```properties
google.api.key=
google.drive.folder.id=
output.folder=
max.concurrency.batch.size=10
periodic.check.time.in.mins=5
events.threshold=10
```
- The Google Drive folder ID can be found in the URL on opening a Drive Folder.

### Code Coverage

The overall code coverage is 80%.

![Code Coverage](/assets/test_coverage.png)

### Benchmarks

| Total Images | Batch Size | Concurrency | Average File Size | Total Time |
|--------------|------------|-------------|-------------------|------------|
| 100          | 10         | 10          | 100KB             | 18 secs    |
| 100          | 50         | 10          | 100KB             | 102 secs   |
| 100          | 50         | 20          | 100KB             | 64 secs    |
| 100          | 100        | 10          | 100KB             | 101 secs   |
| 100          | 100        | 20          | 100KB             | 55 secs    |