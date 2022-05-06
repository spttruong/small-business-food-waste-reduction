package org.foodwaste.datagenerator

import scala.util.Random
import scala.collection.immutable.LazyList

object MockDataGenerator {
    

    val rnd = new Random

    case class FoodWasteRecord(
        timestamp: String,
        recordedBy: String,
        storeId: Int,
        foodCategory: String,
        wasteType: String,
        lossReason: String,
        quantity: Int
    )

    def getRandomElement[A](seq: Seq[A], rnd: Random): A = 
        seq(rnd.nextInt(seq.length))

    def randomIntBetween(start: Int, end: Int)(rnd: Random): Int = {
        // generates random int between start and end input params (both inclusive)
        start + rnd.nextInt( (end - start) + 1 ) 
    }

    def randDateTime: String  = {
        val randMonth = randomIntBetween(1,12)(rnd)
        def randDateStream(month: Int): Int = month match {
            case 1 | 3 | 5 | 7 | 8 | 10 | 12  => randomIntBetween(1,31)(rnd) // 1 to 31
            case 2  => randomIntBetween(1,29)(rnd) // 1 to 28, doesn't account for leap years
            case 4 | 6 | 9 | 11 => randomIntBetween(1,30)(rnd) // 1 to 30
        }

        val randYear = randomIntBetween(2020,2021)(rnd) // up to 2021
        val randHours = randomIntBetween(0,23)(rnd) // up to 23 hours
        val randMinutes = randomIntBetween(0,59)(rnd) // up to 59 mins
        val randSeconds = randomIntBetween(0,59)(rnd) // up to 59 seconds
        
        val tmpMonth = randMonth
        val month = tmpMonth match {
            case tmpMonth if tmpMonth <= 9 => s"0$tmpMonth" // adds leading 0
            case _ => tmpMonth.toString()
        }

        val tmpdate = randDateStream(tmpMonth) 
        val date = tmpdate match {
            case tmpdate if tmpdate <= 9 => s"0$tmpdate" // adds leading 0
            case _ => tmpdate.toString()
        }

        val timestamp = s"${month}/${date}/$randYear $randHours:$randMinutes:$randSeconds"
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
    
    def randEmployee = getRandomElement(sampleEmployeesList, rnd)

    def randStoreId = randomIntBetween(100,105)(rnd) // 5 stores

    def randFoodCategory = getRandomElement(foodCategories, rnd)

    def wasteType(foodCategory: String): String = foodCategory match {
        case "Prepared Meal" => "Post-consumer"
        case _ => "Pre-consumer"
    }

    def randLossReason(wasteType: String) = wasteType match {
        case "Post-consumer" => "Customer Meal Disposal"
        case _ => getRandomElement(lossReasons, rnd)
    }

    def randQuantity = randomIntBetween(1,320)(rnd) // max 20 lbs (320 oz)

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

