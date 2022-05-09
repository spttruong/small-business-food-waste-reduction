# small-business-food-waste-reduction

**NOTE**: this program will create a new directory that will house your output files at `src/main/resources/foodwaste_aggregate_output/<filename>.csv`

If an output directory with the same name as the folder already exists, the program will not work.

## Food Waste Metrics: Introduction

Goal: Use batch processing and Apache Spark to quantify the main causes of food waste for a given business

The scope of this program is for small businesses that lack a basic data pipeline to collect and process this kind of data. Bigger corporations likely already have some kind of internal system that tracks these metrics.

In short, the program will read food waste metrics data in and perform some Spark DataFrame aggregations on the data, producing new table views for downstream analytics.

At this time, this program has been configured to process `.csv` files ONLY.

## Mock Data Generator

A mock data generator, `MakeMockData.scala` is provided for demo purposes to see how the batch processing works.

**Usage**: `sbt "runMain org.foodwaste.datagenerator.MakeMockData <num_of_records>"`

This takes one command line argument, which defines how many records of mock data you wish to generate.

The mock data will output to `src/main/resources/mock_data_output/`.

Again, make sure that this directory does NOT exist on your local machine if you want to run the mock data generator. If it already exists, check for the mock data file `mockdata.csv`. If it's already there, then there is no need to run the generator unless you want to generate a different number of records.

## Spark Food Waste Metrics

Spark reads an input file as specified in `src/main/resources/application.conf` and performs some data transformations and aggregations:

Spark will filter the data:

- by year and by month (if specified by the user)
- among different waste categories (pre vs post-consumer waste)
- food groups (e.g. meat products, dairy, liquids, produce)
- loss reasons (e.g. poor storage, contamination, expiration)

Spark will also aggregate the data and give us sums of the quantities (in ounces) related to each of these filters. The ounce measurement can then be converted by a data analyst to lbs or tons or any other mass or weight related measurement to suit business needs.

**Usage**: `sbt "runMain org.foodwaste.wastemetrics.SparkFoodWasteAggregateJob"`

There will be some prompts for user input in the command line terminal to specify whether the user would like to filter the data by `year` and `month`.

Your output files should be written to `src/main/resources/foodwaste_aggregate_output/<filename>.csv`.

This can be changed in `application.conf`.

### Refernces:

**Special thanks to these resources and their creators:**

Helping me understand how dependencies worked and how to load them into a Scala project:

- Prof. Edward Sumitra's Week 3 Lecture
- https://alvinalexander.com/scala/sbt-how-to-manage-project-dependencies-in-scala/

Contains multiple Spark-related helper methods, including one to modify the name of output files:

- `spark-daria`: https://github.com/MrPowers/spark-daria/

Spark Documentation:
https://spark.apache.org/docs/
