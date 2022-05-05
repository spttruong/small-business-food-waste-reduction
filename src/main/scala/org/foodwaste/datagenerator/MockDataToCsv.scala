package org.foodwaste.datagenerator

import org.foodwaste.config.ConfigUtils

//case class MockDataToCsvConfig(name: String, outputPathToCsvFile: String)

object MockDataToCsv {
 
    def main(args: Array[String]) : Unit = {
        // create data
        //implicit val appSettings = ConfigUtils.loadAppConfig[MockDataToCsvConfig]("org.foodwaste.mock-data-to-csv")
        val mockDataStream = MockDataGenerator.mockData
        val generatedData = mockDataStream.take(args(0).toInt).toList
    }

    def listToCsv(dataList: List[MockDataGenerator.FoodWasteRecord]): Unit = {
        
    }
}

