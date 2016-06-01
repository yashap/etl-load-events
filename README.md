# Event Loading Job

## Introduction
This project is part of an example data pipeline, presented as part of a talk at ACM's [Applicative 2016 Conference]
(http://applicative.acm.org/speakers/ypodeswa.html). Slides are available [here]
(https://docs.google.com/presentation/d/1hX_fPTu92YBIny6LwvUfyF597YT6Bu0F7TgLr6focGk/edit?usp=sharing), which describe
the data pipeline. This pipeline is made of 3 projects, all meant to be stitched together:

* This event loading job, which reads JSON events from S3, and loads them into different database tables based on the
class of event (organization payments, generic organization events, generic user events, and unknown events)
* A job that calculates [organization statistics](https://github.com/yashap/etl-organization-stats), including key stats
like how much each organization is paying, how active the users in the org are, etc. These stats could be used by an
Account Manager to monitor the health of an organization. It depends on the output of the event loading job
* [An implementation](https://github.com/yashap/airflow) of [Airbnb's Airflow system](http://nerds.airbnb.com/airflow/),
which acts as a communication and orchestration layer. It runs the jobs, making sure the the **event loading** job runs
before the **organization statistics** job, and also handles things like job retries, job concurrency levels, and
monitoring/alerting on failures

Note that this is meant to be somewhat of a skeleton pipeline - fork it and use the code as a starting point, tweaking
it for your own needs, with your own business logic.

## Configuration/Setup
Change the AWS credentials in `src/main/resources/dev.conf` to credentials that can access an S3 bucket where you'll
store JSON data (the input to this ETL).  Then, run `./s3_load` to load the sample data from the `sample-data` dir to
your chosen S3 bucket (run `./s3_load -h` for command line args).  In dev, the app will read data from S3 locations
specified in `src/main/resources/dev.conf`, parse/sort them, and load them into appropriate tables an in-memory
database.

If you wish to deploy and run the app in production, you'll need to set up a database and Lambda function.  Spin up a
relational database (i.e. MySQL, PostgreSQL), create the tables shown in `src/main/resources/dev-data.sql`, and update
`src/main/resources/production.conf` with the appropriate credentials.

In prod you'll also be deploying to AWS Lambda. Set up a new Lambda function called `etl-load-events`, with 500+ MB
memory, that runs jars on Java 8 (can be done with a few clicks in the AWS web interface). Put credentials that can
deploy to Lambda in your `~/.aws/credentials` file, then run `./build_and_deploy`, which will build this project and
deploy it to AWS Lambda. You may need to modify `./build_and_deploy` to reference the appropriate profile in
`~/.aws/credentials`. 

## Running in Dev
Once you've done the configuration/setup, simply run a command like `sbt "run --runDate 2016-05-10"`. This will run the
app against all files for that day. It will read these files from S3, parse/sort them, and load them into appropriate
tables an in-memory database.

## Running in Production
Once you've done the configuration/setup, you can simply run the job in the Lambda with an input like:

    { "runDate" : "2016-05-10" }

This can easily be done with the `Test` button in the Lambda web interface, but the job is really intended to be run by
the [Airflow companion project]().
