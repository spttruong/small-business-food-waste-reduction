package org.foodwaste.datagenerator

import org.foodwaste.datagenerator.MockDataGenerator
import org.foodwaste.datagenerator.MockDataGenerator.FoodWasteRecord
import com.typesafe.scalalogging.{LazyLogging}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{Dataset}
import org.foodwaste.utils.SparkUtils

object MakeMockData {
 
    def main(args: Array[String]) : Unit = {
        // create data
        val mockDataStream = MockDataGenerator.mockData
        val generatedData = mockDataStream.take(args(0).toInt).toList
        // spark instance
        val spark = SparkUtils.sparkSession("spark-food-waste-records-generator", "local[*]")
        // convert data to DF and save as .CSV
        val generatedDataDF = listToDs(spark)(generatedData)
        DfToCsv(generatedDataDF)
        spark.stop()
    }

    def listToDs(spark: SparkSession)(dataList: List[FoodWasteRecord]): Dataset[FoodWasteRecord] = {
        import spark.implicits._

        val dataRDD = spark.sparkContext.parallelize(dataList)
        val ds = dataRDD.toDS()
        ds.show()
        ds
    }

    def DfToCsv(ds: Dataset[FoodWasteRecord]): Unit = {
        ds.coalesce(1).write.csv("src/main/resources/sample-food-waste-records.csv")
    }
}
