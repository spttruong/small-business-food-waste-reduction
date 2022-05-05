package org.foodwaste.datagenerator

import org.foodwaste.testutils.{ StandardTest }
import MockDataGenerator.FoodWasteRecord

class MockDataGeneratorTest extends StandardTest {
    "MockDataGenerator" when {

        // test data
        val foodWasteRecordStream = MockDataGenerator.mockData // lazy list of food waste records
        val foodWasteRecordsList = foodWasteRecordStream.take(20).toList

        "generating list mock data of records" should {
            
            "return a list of FoodWasteRecord(s)" in {
                foodWasteRecordsList should not be empty

                for (record <- foodWasteRecordsList) {
                    record shouldBe a [FoodWasteRecord]
                    record.timestamp shouldBe a [String]
                    record.quantity should be  <= 320
                    record.quantity should be >= 0
                    record.storeId should be <= 105
                    record.storeId should be >= 100
                }
            }
            "contain same number of records as specified by sampling" in {
                foodWasteRecordsList should have length 20
            }
        }
    }
}

