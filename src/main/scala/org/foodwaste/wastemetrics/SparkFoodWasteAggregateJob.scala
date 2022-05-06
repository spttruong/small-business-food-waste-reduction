package org.foodwaste.wastemetrics

import org.apache.spark.sql.SparkSession
import org.foodwaste.config.{ConfigUtils}
import org.foodwaste.utils.{SparkUtils}
import org.apache.spark.sql.DataFrame
import pureconfig.generic.auto._
import com.github.mrpowers.spark.daria.sql.DariaWriters.writeSingleFile
import scala.io.StdIn.readLine

case class SparkFoodWasteAggregateJobConfig(
    name: String,
    masterUrl: String,
    inputPathFile: String,
    outputPathCsv: String,
    outputPathParquet: String
)

object SparkFoodWasteAggregateJob {

    def main(args: Array[String]): Unit = {
        implicit val appSettings = ConfigUtils.loadAppConfig[SparkFoodWasteAggregateJobConfig]("org.foodwaste.spark-foodwaste-aggregate-job")
        val spark = SparkUtils.sparkSession(appSettings.name, appSettings.masterUrl)
        runJob(spark)
        spark.stop()
    }

    // populate with instructions here
    def runJob(spark: SparkSession)(implicit conf: SparkFoodWasteAggregateJobConfig): Unit = {
        val records = loadData(spark)
        val dateSplit = splitDate(records)
        dateSplit.show()

        print("Would you like to filter by year? (y/n): ")
        val response1 = readLine()
        if (response1.toLowerCase.matches("y") | response1.toLowerCase.matches("yes")) {
            
            print("Filter by which year? Enter a year with the format yyyy: ")
            val year = readLine().toInt

            // filter df and write output csv by year
            val recordsByYear = foodRecordsByYear(dateSplit, year)

            print("Filter by month? (y/n): ")
            val response2 = readLine()
            
            if (response2.toLowerCase.matches("y") | response2.toLowerCase.matches("yes")) {
                
                print("Filter by which month? Enter an Int between 1 and 12: ")
                val month = readLine().toInt
                
                // filter df and write output csv by month
                val recordsByYearMonth = foodRecordsByMonth(recordsByYear, month)
                // aggregate sum weight of all waste pre vs post consumer
                val aggregateQuantity = aggregateByWasteCategory(recordsByYearMonth)
                // split by pre and post consumer waste
                val preConsumerWasteRecords = returnPreConsumerRecords(recordsByYearMonth)
                val postConsumerWasteRecords = returnPostConsumerRecords(recordsByYearMonth)
                // aggregate waste by food category
                val aggregateQuantityByFood = aggregateByFoodCategory(preConsumerWasteRecords)
                // aggregate waste by loss reason
                val aggregateQuantityByLoss = aggregateByLossReason(preConsumerWasteRecords)

                // create output CSV files
                DfToCsv(spark, aggregateQuantity, "aggregateQuantity")
                DfToCsv(spark, preConsumerWasteRecords, "preConsumerWasteRecords")
                DfToCsv(spark, postConsumerWasteRecords, "postConsumerWasteRecords")
                DfToCsv(spark, aggregateQuantityByFood, "aggregateQuantityByFood")
                DfToCsv(spark, aggregateQuantityByLoss, "aggregateQuantityByLoss")
            } 
            // responded yes to year, but no to month
            else if (response2.toLowerCase.matches("n") | response2.toLowerCase.matches("no")) {
                val aggregateQuantity = aggregateByWasteCategory(recordsByYear)
                val preConsumerWasteRecords = returnPreConsumerRecords(recordsByYear)
                val postConsumerWasteRecords = returnPostConsumerRecords(recordsByYear)
                val aggregateQuantityByFood = aggregateByFoodCategory(recordsByYear)
                val aggregateQuantityByLoss = aggregateByLossReason(recordsByYear)

                DfToCsv(spark, aggregateQuantity, "aggregateQuantity")
                DfToCsv(spark, preConsumerWasteRecords, "preConsumerWasteRecords")
                DfToCsv(spark, postConsumerWasteRecords, "postConsumerWasteRecords")
                DfToCsv(spark, aggregateQuantityByFood, "aggregateQuantityByFood")
                DfToCsv(spark, aggregateQuantityByLoss, "aggregateQuantityByLoss")
            }
            else {
                println("invalid response")
                System.exit(1)
            }
        } 
        // responded no to filtering by year
        else if (response1.toLowerCase.matches("n") | response1.toLowerCase.matches("no")) {
            val aggregateQuantity = aggregateByWasteCategory(records)
            val preConsumerWasteRecords = returnPreConsumerRecords(records)
            val postConsumerWasteRecords = returnPostConsumerRecords(records)
            val aggregateQuantityByFood = aggregateByFoodCategory(records)
            val aggregateQuantityByLoss = aggregateByLossReason(records)

            DfToCsv(spark, aggregateQuantity, "aggregateQuantity")
            DfToCsv(spark, preConsumerWasteRecords, "preConsumerWasteRecords")
            DfToCsv(spark, postConsumerWasteRecords, "postConsumerWasteRecords")
            DfToCsv(spark, aggregateQuantityByFood, "aggregateQuantityByFood")
            DfToCsv(spark, aggregateQuantityByLoss, "aggregateQuantityByLoss")
        } 
        else {
            println("invalid response")
            System.exit(1)
        }
    }


    def loadData(spark: SparkSession)(implicit conf: SparkFoodWasteAggregateJobConfig): DataFrame = {
        import org.apache.spark.sql.types._

        val foodwasteRecordsSchema = StructType( Array(
                 StructField("timestamp", StringType,true),
                 StructField("recordedBy", StringType,true),
                 StructField("storeId", IntegerType,true),
                 StructField("foodCategory", StringType,true),
                 StructField("wasteType", StringType,true),
                 StructField("lossReason", StringType,true),
                 StructField("quantity", IntegerType,true)
             ))  

        val df = spark
        .read
        .format("csv")
        .option("header","false")
        .schema(foodwasteRecordsSchema)
        .load(conf.inputPathFile)
        df
    }

    def splitDate(df: DataFrame): DataFrame = {
        import org.apache.spark.sql.functions._
        // NOTE: substring() start position is 1 based index not 0
        val tmpdf = df.select(col("*"), substring(col("timestamp"), 7, 4).as("year"))
        val dateSplitDf = tmpdf.select(col("*"), substring(col("timestamp"), 1, 2).as("month"))
        dateSplitDf
    }

    def foodRecordsByYear(df: DataFrame, year: Int): DataFrame = {
        df.filter(df("year") === year.toString())
    }

    def foodRecordsByMonth(df: DataFrame, month: Int): DataFrame = {
        val tmpMonth = month match {
            case month if month <= 9 => s"0$month"
            case _ => month.toString()
        }
        
        df.filter(df("month") === tmpMonth)
    }

    def returnPreConsumerRecords(df: DataFrame): DataFrame = {
        df.filter(df("wasteType") === "Pre-consumer")
    }

    def returnPostConsumerRecords(df: DataFrame): DataFrame = {
        df.filter(df("wasteType") === "Post-consumer")
    }

    def aggregateByFoodCategory(df: DataFrame): DataFrame = {
        import org.apache.spark.sql.functions._
        df.groupBy("foodCategory").agg(sum("quantity"))
    }

    def aggregateByWasteCategory(df: DataFrame): DataFrame = {
        import org.apache.spark.sql.functions._
        df.groupBy("wasteType").agg(sum("quantity"))
    }

    def aggregateByLossReason(df: DataFrame): DataFrame = {
        import org.apache.spark.sql.functions._
        df.groupBy("lossReason").agg(sum("quantity"))
    }

    def DfToCsv(spark: SparkSession, df: DataFrame, csvName: String)(implicit conf: SparkFoodWasteAggregateJobConfig): Unit = {
        writeSingleFile(
            df = df,
            format = "csv",
            sc = spark.sparkContext,
            tmpFolder = conf.outputPathCsv+csvName,
            filename = conf.outputPathCsv+csvName+".csv")
    }
}
