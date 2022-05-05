package org.foodwaste.datagenerator

import scala.util.Random

object MockDataGenerator {
    val rnd = new Random()

    case class FoodWasteRecord(
        timestamp: String,
        recordedBy: String,
        storeId: Int,
        foodCategory: String,
        wasteType: String,
        lossReason: String,
        quantity: Int
    )

    def getRandomElement[A](seq: Seq[A], random: Random): A = 
        seq(random.nextInt(seq.length))

    val randDateTime: String  = {
        val randMonth = rnd.between(1,13)
        def randDateStream(month: Int): Int = month match {
            case 1 | 3 | 5 | 7 | 8 | 10 | 12  => rnd.between(1,32) // 1 to 31
            case 2  => rnd.between(1,29) // 1 to 28, doesn't account for leap years
            case 4 | 6 | 9 | 11 => rnd.between(1,31) // 1 to 30
        }
        val randYear = rnd.between(2020,2022) // up to 2021
        val randHours = rnd.between(0,24) // up to 23 hours
        val randMinutes = rnd.between(0,60) // up to 59 mins
        val randSeconds = rnd.between(0,60) // up to 59 seconds
        
        val month = randMonth
        
        val timestamp = s"$month/${randDateStream(month)}/$randYear $randHours:$randMinutes:$randSeconds"
        timestamp

    }

    val sampleEmployeesList = Seq("Bryan", "Emma", "Joe", "Christy", "Steve", "Danielle")
    
    val foodCategories = Seq(
                            "Beer, Wine & Spirits",
                            "Beverages",
                            "Bread & Bakery",
                            "Breafast & Cereal",
                            "Canned Good & Soups",
                            "Condiments/Spices & Bake",
                            "Cookies, Snacks & Candy",
                            "Dairy, Eggs & Cheese",
                            "Deli",
                            "Frozen Foods",
                            "Produce: Fruits and Vegetables",
                            "Grains & Pasta",
                            "Meat",
                            "Seafood",
                            "Prepared Meal")

    val lossReasons = Seq("Excess/over-order", "Expiration", "Improper Storage", "Storage Failure", "Contamination")
    
    val randEmployee = getRandomElement(sampleEmployeesList, rnd)

    val randStoreId = rnd.between(100,106) // 5 stores

    val randFoodCategory = getRandomElement(foodCategories, rnd)

    def wasteType(foodCategory: String): String = foodCategory match {
        case "Prepared Meal" => "Post-consumer"
        case _ => "Pre-consumer"
    }

    def randLossReason(wasteType: String) = wasteType match {
        case "Post-consumer" => "Customer Meal Disposal"
        case _ => getRandomElement(lossReasons, rnd)
    }

    val randQuantity = rnd.between(1,321) // max 20 lbs (320 oz)

    val mockData: LazyList[FoodWasteRecord] = LazyList.continually {
        
        val food = randFoodCategory
        val consumerWasteType = wasteType(food)

        FoodWasteRecord(randDateTime,
                        randEmployee,
                        randStoreId,
                        food,
                        consumerWasteType,
                        randLossReason(consumerWasteType),
                        randQuantity)
    }
}
