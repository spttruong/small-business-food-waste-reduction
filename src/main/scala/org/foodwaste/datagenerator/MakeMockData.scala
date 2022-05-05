package org.foodwaste.datagenerator

import org.foodwaste.datagenerator.MockDataGenerator
import org.foodwaste.datagenerator.MockDataGenerator.FoodWasteRecord
// import com.typesafe.scalalogging.{LazyLogging}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{Dataset}
import org.apache.spark.sql.{DataFrame}
import org.foodwaste.utils.SparkUtils
import com.github.mrpowers.spark.daria.sql.DariaWriters.writeSingleFile

object MakeMockData {
 
    def main(args: Array[String]) : Unit = {
        // create data
        val mockDataStream = MockDataGenerator.mockData
        val generatedData = mockDataStream.take(args(0).toInt).toList
        // spark instance
        val spark = SparkUtils.sparkSession("spark-food-waste-records-generator", "local[*]")
        // convert data to DF and save as .CSV
        val generatedDataDF = listToDf(spark)(generatedData)
        DfToCsv(spark)(generatedDataDF)
        spark.stop()
    }

    def listToDs(spark: SparkSession)(dataList: List[FoodWasteRecord]): Dataset[FoodWasteRecord] = {
        import spark.implicits._

        val dataRDD = spark.sparkContext.parallelize(dataList)
        val ds = dataRDD.toDS()
        ds
    }

    def listToDf(spark: SparkSession)(dataList: List[FoodWasteRecord]): DataFrame = {
        import spark.implicits._
        val dataRDD = spark.sparkContext.parallelize(dataList)
        val df = dataRDD.toDF()
        df
    }

    def DsToCsv(ds: Dataset[FoodWasteRecord]): Unit = {
        ds.coalesce(1).write.csv("src/main/resources/sample-food-waste-records/")
    }

    def DfToCsv(spark: SparkSession)(df: DataFrame): Unit = {
        writeSingleFile(
            df = df,
            format = "csv",
            sc = spark.sparkContext,
            tmpFolder = "src/main/resources/mock_data_output/",
            filename = "src/main/resources/mock_data_output/mockdata.csv")
    }
}
