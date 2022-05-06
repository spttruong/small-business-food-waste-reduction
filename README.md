# small-business-food-waste-reduction

## Spark Food Waste Metrics



## Mock Data Generator

### Usage

(WIP)

When inside the project root directory, run the following command to generate mock data if there isn't some already available `src/main/resources/`.

`sbt "runMain org.foodwaste.datagenerator.MakeMockData <number of records>"`

### Refernces:

**Special thanks to these resources and their creators:**

Helping me understand how dependencies worked and how to load them into a Scala project:
- Prof. Edward Sumitra's Week 3 Lecture
- https://alvinalexander.com/scala/sbt-how-to-manage-project-dependencies-in-scala/

Contains multiple Spark-related helper methods, including one to modify the name of output files:
- `spark-daria`: https://github.com/MrPowers/spark-daria/

Spark Documentation:
https://spark.apache.org/docs/
