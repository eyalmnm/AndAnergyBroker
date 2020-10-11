package com.usenergysolutions.energybroker.config

import android.util.Log
import com.usenergysolutions.energybroker.R
import com.usenergysolutions.energybroker.model.PlaceTypeModel

class LocationHelper {
    companion object {

        // Hold Types Models Array
        private var typeArray: ArrayList<PlaceTypeModel>? = null

        // Places Constants
        val TYPE_ACCOUNTING: Int = 1
        val TYPE_AIRPORT: Int = 2
        val TYPE_AMUSEMENT_PARK: Int = 3
        val TYPE_AQUARIUM: Int = 4
        val TYPE_ART_GALLERY: Int = 5
        val TYPE_ATM: Int = 6
        val TYPE_BAKERY: Int = 7
        val TYPE_BANK: Int = 8
        val TYPE_BAR: Int = 9
        val TYPE_BEAUTY_SALON: Int = 10
        val TYPE_BICYCLE_STORE: Int = 11
        val TYPE_BOOK_STORE: Int = 12
        val TYPE_BOWLING_ALLEY: Int = 13
        val TYPE_BUS_STATION: Int = 14
        val TYPE_CAFE: Int = 15
        val TYPE_CAMPGROUND: Int = 16
        val TYPE_CAR_DEALER: Int = 17
        val TYPE_CAR_RENTAL: Int = 18
        val TYPE_CAR_REPAIR: Int = 19
        val TYPE_CAR_WASH: Int = 20
        val TYPE_CASINO: Int = 21
        val TYPE_CEMETERY: Int = 22
        val TYPE_CHURCH: Int = 23
        val TYPE_CITY_HALL: Int = 24
        val TYPE_CLOTHING_STORE: Int = 25
        val TYPE_COLLOQUIAL_AREA: Int = 1004
        val TYPE_CONVENIENCE_STORE: Int = 26
        val TYPE_COUNTRY: Int = 1005
        val TYPE_COURTHOUSE: Int = 27
        val TYPE_DENTIST: Int = 28
        val TYPE_DEPARTMENT_STORE: Int = 29
        val TYPE_DOCTOR: Int = 30
        val TYPE_ELECTRICIAN: Int = 31
        val TYPE_ELECTRONICS_STORE: Int = 32
        val TYPE_EMBASSY: Int = 33
        val TYPE_ESTABLISHMENT: Int = 34
        val TYPE_FINANCE: Int = 35
        val TYPE_FIRE_STATION: Int = 36
        val TYPE_FLOOR: Int = 1006
        val TYPE_FLORIST: Int = 37
        val TYPE_FOOD: Int = 38
        val TYPE_FUNERAL_HOME: Int = 39
        val TYPE_FURNITURE_STORE: Int = 40
        val TYPE_GAS_STATION: Int = 41
        val TYPE_GENERAL_CONTRACTOR: Int = 42
        val TYPE_GEOCODE: Int = 1007
        val TYPE_GROCERY_OR_SUPERMARKET: Int = 43
        val TYPE_GYM: Int = 44
        val TYPE_HAIR_CARE: Int = 45
        val TYPE_HARDWARE_STORE: Int = 46
        val TYPE_HEALTH: Int = 47
        val TYPE_HINDU_TEMPLE: Int = 48
        val TYPE_HOME_GOODS_STORE: Int = 49
        val TYPE_HOSPITAL: Int = 50
        val TYPE_INSURANCE_AGENCY: Int = 51
        val TYPE_INTERSECTION: Int = 1008
        val TYPE_JEWELRY_STORE: Int = 52
        val TYPE_LAUNDRY: Int = 53
        val TYPE_LAWYER: Int = 54
        val TYPE_LIBRARY: Int = 55
        val TYPE_LIQUOR_STORE: Int = 56
        val TYPE_LOCALITY: Int = 1009
        val TYPE_LOCAL_GOVERNMENT_OFFICE: Int = 57
        val TYPE_LOCKSMITH: Int = 58
        val TYPE_LODGING: Int = 59
        val TYPE_MEAL_DELIVERY: Int = 60
        val TYPE_MEAL_TAKEAWAY: Int = 61
        val TYPE_MOSQUE: Int = 62
        val TYPE_MOVIE_RENTAL: Int = 63
        val TYPE_MOVIE_THEATER: Int = 64
        val TYPE_MOVING_COMPANY: Int = 65
        val TYPE_MUSEUM: Int = 66
        val TYPE_NATURAL_FEATURE: Int = 1010
        val TYPE_NEIGHBORHOOD: Int = 1011
        val TYPE_NIGHT_CLUB: Int = 67
        val TYPE_OTHER: Int = 0
        val TYPE_PAINTER: Int = 68
        val TYPE_PARK: Int = 69
        val TYPE_PARKING: Int = 70
        val TYPE_PET_STORE: Int = 71
        val TYPE_PHARMACY: Int = 72
        val TYPE_PHYSIOTHERAPIST: Int = 73
        val TYPE_PLACE_OF_WORSHIP: Int = 74
        val TYPE_PLUMBER: Int = 75
        val TYPE_POINT_OF_INTEREST: Int = 1013
        val TYPE_POLICE: Int = 76
        val TYPE_POLITICAL: Int = 1012
        val TYPE_POSTAL_CODE: Int = 1015
        val TYPE_POSTAL_CODE_PREFIX: Int = 1016
        val TYPE_POSTAL_TOWN: Int = 1017
        val TYPE_POST_BOX: Int = 1014
        val TYPE_POST_OFFICE: Int = 77
        val TYPE_PREMISE: Int = 1018
        val TYPE_REAL_ESTATE_AGENCY: Int = 78
        val TYPE_RESTAURANT: Int = 79
        val TYPE_ROOFING_CONTRACTOR: Int = 80
        val TYPE_ROOM: Int = 1019
        val TYPE_ROUTE: Int = 1020
        val TYPE_RV_PARK: Int = 81
        val TYPE_SCHOOL: Int = 82
        val TYPE_SHOE_STORE: Int = 83
        val TYPE_SHOPPING_MALL: Int = 84
        val TYPE_SPA: Int = 85
        val TYPE_STADIUM: Int = 86
        val TYPE_STORAGE: Int = 87
        val TYPE_STORE: Int = 88
        val TYPE_STREET_ADDRESS: Int = 1021
        val TYPE_SUBLOCALITY: Int = 1022
        val TYPE_SUBLOCALITY_LEVEL_1: Int = 1023
        val TYPE_SUBLOCALITY_LEVEL_2: Int = 1024
        val TYPE_SUBLOCALITY_LEVEL_3: Int = 1025
        val TYPE_SUBLOCALITY_LEVEL_4: Int = 1026
        val TYPE_SUBLOCALITY_LEVEL_5: Int = 1027
        val TYPE_SUBPREMISE: Int = 1028
        val TYPE_SUBWAY_STATION: Int = 89
        val TYPE_SYNAGOGUE: Int = 90
        val TYPE_SYNTHETIC_GEOCODE: Int = 1029
        val TYPE_TAXI_STAND: Int = 91
        val TYPE_TRAIN_STATION: Int = 92
        val TYPE_TRANSIT_STATION: Int = 1030
        val TYPE_TRAVEL_AGENCY: Int = 93
        val TYPE_UNIVERSITY: Int = 94
        val TYPE_VETERINARY_CARE: Int = 95
        val TYPE_ZOO: Int = 96

        fun getImageByPlaceType(placeType: Int): Int {
            when (placeType) {
                TYPE_AIRPORT -> return R.drawable.ic_airport_71
                TYPE_AMUSEMENT_PARK -> return R.drawable.ic_amusement_71
                TYPE_AQUARIUM -> return R.drawable.ic_aquarium_71
                TYPE_ART_GALLERY -> return R.drawable.ic_art_gallery_71
                TYPE_ATM -> return R.drawable.ic_art_gallery_71
                TYPE_BAKERY -> return R.drawable.ic_cafe_71
                TYPE_BANK -> return R.drawable.ic_bank_dollar_71
                TYPE_BAR -> return R.drawable.ic_bar_71
                TYPE_BEAUTY_SALON -> return R.drawable.ic_generic_business_71
                TYPE_BICYCLE_STORE -> return R.drawable.ic_bicycle_71
                TYPE_BOOK_STORE -> return R.drawable.ic_shopping_71
                TYPE_BOWLING_ALLEY -> return R.drawable.ic_bowling_71
                TYPE_BUS_STATION -> return R.drawable.ic_bus_71
                TYPE_CAFE -> return R.drawable.ic_cafe_71
                TYPE_CAMPGROUND -> return R.drawable.ic_camping_71
                TYPE_CAR_DEALER -> return R.drawable.ic_car_dealer_71
                TYPE_CAR_RENTAL -> return R.drawable.ic_car_rental_71
                TYPE_CAR_REPAIR -> return R.drawable.ic_car_repair_71
                TYPE_CAR_WASH -> return R.drawable.ic_car_repair_71
                TYPE_CASINO -> return R.drawable.ic_casino_71
                TYPE_CEMETERY -> return R.drawable.ic_worship_christian_71
                TYPE_CHURCH -> return R.drawable.ic_worship_christian_71
                TYPE_CITY_HALL -> return R.drawable.ic_civic_building_71
                TYPE_CLOTHING_STORE -> return R.drawable.ic_shopping_71
                TYPE_COLLOQUIAL_AREA -> return R.drawable.ic_convenience_71
                TYPE_CONVENIENCE_STORE -> return R.drawable.ic_convenience_71
                TYPE_COUNTRY -> return R.drawable.ic_government_71
                TYPE_COURTHOUSE -> return R.drawable.ic_courthouse_71
                TYPE_DENTIST -> return R.drawable.ic_dentist_71
                TYPE_DEPARTMENT_STORE -> return R.drawable.ic_shopping_71
                TYPE_DOCTOR -> return R.drawable.ic_doctor_71
                TYPE_ELECTRICIAN -> R.drawable.ic_electronics_71
                TYPE_ELECTRONICS_STORE -> R.drawable.ic_electronics_71
                TYPE_EMBASSY -> return R.drawable.ic_government_71
                TYPE_ESTABLISHMENT -> R.drawable.ic_government_71
                TYPE_FINANCE -> return R.drawable.ic_bank_dollar_71
                TYPE_FIRE_STATION -> return R.drawable.ic_geocode_71
                TYPE_FLOOR -> return R.drawable.ic_flower_71
                TYPE_FLORIST -> return R.drawable.ic_flower_71
                TYPE_FOOD -> return R.drawable.ic_restaurant_71
                TYPE_FUNERAL_HOME -> R.drawable.ic_worship_christian_71
                TYPE_FURNITURE_STORE -> R.drawable.ic_generic_business_71
                TYPE_GAS_STATION -> return R.drawable.ic_gas_station_71
                TYPE_GENERAL_CONTRACTOR -> R.drawable.ic_generic_business_71
                TYPE_GEOCODE -> return R.drawable.ic_geocode_71
                TYPE_GROCERY_OR_SUPERMARKET -> return R.drawable.ic_supermarket_71
                TYPE_GYM -> return R.drawable.ic_fitness_71
                TYPE_HAIR_CARE -> return R.drawable.ic_barber_71
                TYPE_HARDWARE_STORE -> return R.drawable.ic_generic_business_71
                TYPE_HEALTH -> return R.drawable.ic_doctor_71
                TYPE_HINDU_TEMPLE -> return R.drawable.ic_worship_hindu_71
                TYPE_HOME_GOODS_STORE -> return R.drawable.ic_generic_recreational_71
                TYPE_HOSPITAL -> return R.drawable.ic_doctor_71
                TYPE_INSURANCE_AGENCY -> return R.drawable.ic_generic_business_71
                TYPE_INTERSECTION -> return R.drawable.ic_geocode_71
                TYPE_JEWELRY_STORE -> return R.drawable.ic_jewelry_71
                TYPE_LAUNDRY -> return R.drawable.ic_generic_business_71
                TYPE_LAWYER -> return R.drawable.ic_generic_business_71
                TYPE_LIBRARY -> return R.drawable.ic_library_71
                TYPE_LIQUOR_STORE -> return R.drawable.ic_bar_71
                TYPE_LOCALITY -> return R.drawable.ic_generic_business_71
                TYPE_LOCAL_GOVERNMENT_OFFICE -> return R.drawable.ic_government_71
                TYPE_LOCKSMITH -> return R.drawable.ic_generic_business_71
                TYPE_LODGING -> return R.drawable.ic_lodging_71
                TYPE_MEAL_DELIVERY -> return R.drawable.ic_restaurant_71
                TYPE_MEAL_TAKEAWAY -> return R.drawable.ic_restaurant_71
                TYPE_MOSQUE -> return R.drawable.ic_worship_islam_71
                TYPE_MOVIE_RENTAL -> return R.drawable.ic_movies_71
                TYPE_MOVIE_THEATER -> return R.drawable.ic_movies_71
                TYPE_MOVING_COMPANY -> return R.drawable.ic_generic_business_71
                TYPE_MUSEUM -> return R.drawable.ic_museum_71
                TYPE_NATURAL_FEATURE -> return R.drawable.ic_generic_recreational_71
                TYPE_NEIGHBORHOOD -> return R.drawable.ic_geocode_71
                TYPE_NIGHT_CLUB -> return R.drawable.ic_bar_71
                TYPE_OTHER -> return R.drawable.ic_geocode_71
                TYPE_PAINTER -> return R.drawable.ic_generic_business_71
                TYPE_PARK -> return R.drawable.ic_generic_recreational_71
                TYPE_PARKING -> return R.drawable.ic_generic_recreational_71
                TYPE_PET_STORE -> return R.drawable.ic_pet_71
                TYPE_PHARMACY -> return R.drawable.ic_doctor_71
                TYPE_PHYSIOTHERAPIST -> return R.drawable.ic_doctor_71
                TYPE_PLACE_OF_WORSHIP -> return R.drawable.ic_worship_general_71
                TYPE_PLUMBER -> return R.drawable.ic_generic_business_71
                TYPE_POINT_OF_INTEREST -> return R.drawable.ic_generic_recreational_71
                TYPE_POLICE -> return R.drawable.ic_police_71
                TYPE_POLITICAL -> return R.drawable.ic_government_71
                TYPE_POSTAL_CODE -> return R.drawable.ic_post_office_71
                TYPE_POSTAL_CODE_PREFIX -> return R.drawable.ic_post_office_71
                TYPE_POST_BOX -> return R.drawable.ic_post_office_71
                TYPE_POST_OFFICE -> return R.drawable.ic_post_office_71
                TYPE_POSTAL_TOWN -> return R.drawable.ic_post_office_71
                TYPE_PREMISE -> return R.drawable.ic_geocode_71
                TYPE_RESTAURANT -> return R.drawable.ic_restaurant_71
                TYPE_REAL_ESTATE_AGENCY -> return R.drawable.ic_travel_agent_71
                TYPE_ROOFING_CONTRACTOR -> return R.drawable.ic_generic_business_71
                TYPE_ROOM -> return R.drawable.ic_lodging_71
                TYPE_ROUTE -> return R.drawable.ic_geocode_71
                TYPE_RV_PARK -> return R.drawable.ic_generic_recreational_71
                TYPE_SCHOOL -> return R.drawable.ic_school_71
                TYPE_SHOE_STORE -> return R.drawable.ic_shopping_71
                TYPE_SHOPPING_MALL -> return R.drawable.ic_shopping_71
                TYPE_SPA -> return R.drawable.ic_fitness_71
                TYPE_STADIUM -> return R.drawable.ic_stadium_71
                TYPE_STORAGE -> return R.drawable.ic_generic_business_71
                TYPE_STORE -> return R.drawable.ic_generic_business_71
                TYPE_STREET_ADDRESS -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY_LEVEL_1 -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY_LEVEL_2 -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY_LEVEL_3 -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY_LEVEL_4 -> return R.drawable.ic_geocode_71
                TYPE_SUBLOCALITY_LEVEL_5 -> return R.drawable.ic_geocode_71
                TYPE_SUBPREMISE -> return R.drawable.ic_generic_business_71
                TYPE_SUBWAY_STATION -> return R.drawable.ic_train_71
                TYPE_SYNAGOGUE -> return R.drawable.ic_worship_jewish_71
                TYPE_SYNTHETIC_GEOCODE -> return R.drawable.ic_geocode_71
                TYPE_TAXI_STAND -> return R.drawable.ic_taxi_71
                TYPE_TRAIN_STATION -> return R.drawable.ic_train_71
                TYPE_TRANSIT_STATION -> return R.drawable.ic_bus_71
                TYPE_TRAVEL_AGENCY -> return R.drawable.ic_travel_agent_71
                TYPE_UNIVERSITY -> return R.drawable.ic_university_71
                TYPE_VETERINARY_CARE -> return R.drawable.ic_pet_71
                TYPE_ZOO -> return R.drawable.ic_zoo_71
                else -> return R.drawable.ic_geocode_71
            }
            return R.drawable.ic_geocode_71
        }

        fun getTypeByTypeName(placeTypeName: String): Int {
            when (placeTypeName.toLowerCase()) {
                "accounting" -> return TYPE_ACCOUNTING
                "airport" -> return TYPE_AIRPORT
                "amusement park" -> return TYPE_AMUSEMENT_PARK
                "aquarium" -> return TYPE_AQUARIUM
                "art gallery" -> TYPE_ART_GALLERY
                "atm" -> return TYPE_ATM
                "bakery" -> return TYPE_BAKERY
                "bank" -> return TYPE_BANK
                "bar" -> return TYPE_BAR
                "beauty salon" -> return TYPE_BEAUTY_SALON
                "bicycle store" -> return TYPE_BICYCLE_STORE
                "book store" -> return TYPE_BOOK_STORE
                "bowling alley" -> return TYPE_BOWLING_ALLEY
                "bus station" -> return TYPE_BUS_STATION
                "cafe" -> return TYPE_CAFE
                "campground" -> return TYPE_CAMPGROUND
                "car dealer" -> return TYPE_CAR_DEALER
                "car rental" -> return TYPE_CAR_RENTAL
                "car repair" -> return TYPE_CAR_REPAIR
                "car wash" -> return TYPE_CAR_WASH
                "casino" -> return TYPE_CASINO
                "cemetery" -> return TYPE_CEMETERY
                "church" -> return TYPE_CHURCH
                "city hall" -> return TYPE_CITY_HALL
                "clothing store" -> return TYPE_CLOTHING_STORE
                "convenience store" -> return TYPE_CONVENIENCE_STORE
                "courthouse" -> return TYPE_COURTHOUSE
                "dentist" -> return TYPE_DENTIST
                "department store" -> return TYPE_DEPARTMENT_STORE
                "doctor" -> return TYPE_DOCTOR
                "electrician" -> return TYPE_ELECTRICIAN
                "electronics store" -> return TYPE_ELECTRONICS_STORE
                "embassy" -> return TYPE_EMBASSY
                "fire station" -> return TYPE_FIRE_STATION
                "florist" -> return TYPE_FLORIST
                "funeral home" -> return TYPE_FUNERAL_HOME
                "furniture store" -> return TYPE_FURNITURE_STORE
                "gas station" -> return TYPE_GAS_STATION
                "gym" -> return TYPE_GYM
                "hair care" -> return TYPE_HAIR_CARE
                "hardware store" -> return TYPE_HARDWARE_STORE
                "hindu temple" -> return TYPE_HINDU_TEMPLE
                "home goods store" -> return TYPE_HOME_GOODS_STORE
                "hospital" -> return TYPE_HOSPITAL
                "insurance agency" -> return TYPE_INSURANCE_AGENCY
                "jewelry store" -> return TYPE_JEWELRY_STORE
                "laundry" -> return TYPE_LAUNDRY
                "lawyer" -> return TYPE_LAWYER
                "library" -> return TYPE_LIBRARY
                "liquor store" -> return TYPE_LIQUOR_STORE
                "local government office" -> return TYPE_LOCAL_GOVERNMENT_OFFICE
                "locksmith" -> return TYPE_LOCKSMITH
                "lodging" -> return TYPE_LODGING
                "meal delivery" -> return TYPE_MEAL_DELIVERY
                "meal takeaway" -> return TYPE_MEAL_TAKEAWAY
                "mosque" -> return TYPE_MOSQUE
                "movie rental" -> return TYPE_MOVIE_RENTAL
                "movie theater" -> return TYPE_MOVIE_THEATER
                "moving company" -> return TYPE_MOVING_COMPANY
                "museum" -> return TYPE_MUSEUM
                "night club" -> return TYPE_NIGHT_CLUB
                "painter" -> return TYPE_PAINTER
                "park" -> return TYPE_PARK
                "parking" -> return TYPE_PARKING
                "pet store" -> return TYPE_PET_STORE
                "pharmacy" -> return TYPE_PHARMACY
                "physiotherapist" -> return TYPE_PHYSIOTHERAPIST
                "plumber" -> return TYPE_PLUMBER
                "police" -> return TYPE_POLICE
                "post office" -> return TYPE_POST_OFFICE
                "real estate agency" -> return TYPE_REAL_ESTATE_AGENCY
                "restaurant" -> return TYPE_RESTAURANT
                "roofing contractor" -> return TYPE_ROOFING_CONTRACTOR
                "rv park" -> return TYPE_RV_PARK
                "school" -> return TYPE_SCHOOL
                "shoe store" -> return TYPE_SHOE_STORE
                "shopping mall" -> return TYPE_SHOPPING_MALL
                "spa" -> return TYPE_SPA
                "stadium" -> return TYPE_STADIUM
                "storage" -> return TYPE_STORAGE
                "store" -> return TYPE_STORE
                "subway station" -> return TYPE_SUBWAY_STATION
                "supermarket" -> return TYPE_GROCERY_OR_SUPERMARKET
                "synagogue" -> return TYPE_SYNAGOGUE
                "taxi stand" -> return TYPE_TAXI_STAND
                "train station" -> return TYPE_TRAIN_STATION
                "transit station" -> return TYPE_TRANSIT_STATION
                "travel agency" -> return TYPE_TRAVEL_AGENCY
                "veterinary care" -> return TYPE_VETERINARY_CARE
                "zoo" -> return TYPE_ZOO
            }
            return TYPE_OTHER
        }

        fun getTypeNameByPlaceType(placeType: Int): String {
            when (placeType) {
                TYPE_AIRPORT -> return "Airport"
                TYPE_AMUSEMENT_PARK -> return "Amusement Park"
                TYPE_AQUARIUM -> return "Aquarium"
                TYPE_ART_GALLERY -> return "Art Gallery"
                TYPE_ATM -> return "ATM"
                TYPE_BAKERY -> return "Bakery"
                TYPE_BANK -> return "Bank"
                TYPE_BAR -> return "Bar"
                TYPE_BEAUTY_SALON -> return "Beauty Salon"
                TYPE_BICYCLE_STORE -> return "Bicycle Store"
                TYPE_BOOK_STORE -> return "Book Store"
                TYPE_BOWLING_ALLEY -> return "Bowling Alley"
                TYPE_BUS_STATION -> return "Bus Station"
                TYPE_CAFE -> return "Cafe"
                TYPE_CAMPGROUND -> return "Camp Ground"
                TYPE_CAR_DEALER -> return "Car Dealer"
                TYPE_CAR_RENTAL -> return "Car Rental"
                TYPE_CAR_REPAIR -> return "Car Repair"
                TYPE_CAR_WASH -> return "Car Wash"
                TYPE_CASINO -> return "Casino"
                TYPE_CEMETERY -> return "Cemetery"
                TYPE_CHURCH -> return "Church"
                TYPE_CITY_HALL -> return "City Hall"
                TYPE_CLOTHING_STORE -> return "Clothing Store"
                TYPE_COLLOQUIAL_AREA -> return "Colloquial Area"
                TYPE_CONVENIENCE_STORE -> return "Convenience Store"
                TYPE_COUNTRY -> return "Country"
                TYPE_COURTHOUSE -> return "Cout House"
                TYPE_DENTIST -> return "Dentist"
                TYPE_DEPARTMENT_STORE -> return "Department Store"
                TYPE_DOCTOR -> return "Doctor"
                TYPE_ELECTRICIAN -> "Electrician"
                TYPE_ELECTRONICS_STORE -> "Electronics Store"
                TYPE_EMBASSY -> return "Embassy"
                TYPE_ESTABLISHMENT -> "Establishment"
                TYPE_FINANCE -> return "Finance"
                TYPE_FIRE_STATION -> return "Fire Station"
                TYPE_FLOOR -> return "Floor"
                TYPE_FLORIST -> return "Florist"
                TYPE_FOOD -> return "Food"
                TYPE_FUNERAL_HOME -> "Funeral House"
                TYPE_FURNITURE_STORE -> "Furniture Store"
                TYPE_GAS_STATION -> return "Gas Station"
                TYPE_GENERAL_CONTRACTOR -> "General Contractor"
                TYPE_GEOCODE -> return "Geo Code"
                TYPE_GROCERY_OR_SUPERMARKET -> return "Grocery or Supermarket"
                TYPE_GYM -> return "Gym"
                TYPE_HAIR_CARE -> return "Hair Care"
                TYPE_HARDWARE_STORE -> return "Hardware Store"
                TYPE_HEALTH -> return "Health"
                TYPE_HINDU_TEMPLE -> return "Hindu Temple"
                TYPE_HOME_GOODS_STORE -> return "Home Goods Store"
                TYPE_HOSPITAL -> return "Hospital"
                TYPE_INSURANCE_AGENCY -> return "Insurance Agency"
                TYPE_INTERSECTION -> return "Intersection"
                TYPE_JEWELRY_STORE -> return "Jewelry Store"
                TYPE_LAUNDRY -> return "Laundry"
                TYPE_LAWYER -> return "Lawyer"
                TYPE_LIBRARY -> return "Library"
                TYPE_LIQUOR_STORE -> return "Liquor Store"
                TYPE_LOCALITY -> return "Locality"
                TYPE_LOCAL_GOVERNMENT_OFFICE -> return "Local Government Office"
                TYPE_LOCKSMITH -> return "Locksmith"
                TYPE_LODGING -> return "Lodging"
                TYPE_MEAL_DELIVERY -> return "Meal Delivery"
                TYPE_MEAL_TAKEAWAY -> return "Meal Take Away"
                TYPE_MOSQUE -> return "Mosque"
                TYPE_MOVIE_RENTAL -> return "Movie Rental"
                TYPE_MOVIE_THEATER -> return "Movie Theater"
                TYPE_MOVING_COMPANY -> return "Moving Company"
                TYPE_MUSEUM -> return "Museum"
                TYPE_NATURAL_FEATURE -> return "Natural Feature"
                TYPE_NEIGHBORHOOD -> return "Neighborhood"
                TYPE_NIGHT_CLUB -> return "Night Club"
                TYPE_OTHER -> return "Other"
                TYPE_PAINTER -> return "Painter"
                TYPE_PARK -> return "Park"
                TYPE_PARKING -> return "Parking"
                TYPE_PET_STORE -> return "Pet Store"
                TYPE_PHARMACY -> return "Pharmacy"
                TYPE_PHYSIOTHERAPIST -> return "Physiotherapist"
                TYPE_PLACE_OF_WORSHIP -> return "Place of Worship"
                TYPE_PLUMBER -> return "Plumber"
                TYPE_POINT_OF_INTEREST -> return "Point of Interest"
                TYPE_POLICE -> return "Police"
                TYPE_POLITICAL -> return "Political"
                TYPE_POSTAL_CODE -> return "Postal Code"
                TYPE_POSTAL_CODE_PREFIX -> return "Postal Code Prefix"
                TYPE_POST_BOX -> return "Post Box"
                TYPE_POST_OFFICE -> return "Post Office"
                TYPE_POSTAL_TOWN -> return "Postal Town"
                TYPE_PREMISE -> return "Permise"
                TYPE_RESTAURANT -> return "Restaurant"
                TYPE_REAL_ESTATE_AGENCY -> return "Real Estate Agency"
                TYPE_ROOFING_CONTRACTOR -> return "Roofing Contractor"
                TYPE_ROOM -> return "Room"
                TYPE_ROUTE -> return "Route"
                TYPE_RV_PARK -> return "RV Park"
                TYPE_SCHOOL -> return "School"
                TYPE_SHOE_STORE -> return "Shoe Store"
                TYPE_SHOPPING_MALL -> return "Shopping Mall"
                TYPE_SPA -> return "Spa"
                TYPE_STADIUM -> return "Stadium"
                TYPE_STORAGE -> return "Storage"
                TYPE_STORE -> return "Store"
                TYPE_STREET_ADDRESS -> return "Street Address"
                TYPE_SUBLOCALITY -> return "Sub Locality"
                TYPE_SUBLOCALITY_LEVEL_1 -> return "Sub Locality Level 1"
                TYPE_SUBLOCALITY_LEVEL_2 -> return "Sub Locality Level 2"
                TYPE_SUBLOCALITY_LEVEL_3 -> return "Sub Locality Level 3"
                TYPE_SUBLOCALITY_LEVEL_4 -> return "Sub Locality Level 4"
                TYPE_SUBLOCALITY_LEVEL_5 -> return "Sub Locality Level 5"
                TYPE_SUBPREMISE -> return "Sub Premise"
                TYPE_SUBWAY_STATION -> return "Subway Station"
                TYPE_SYNAGOGUE -> return "Synagogue"
                TYPE_SYNTHETIC_GEOCODE -> return "Synthetic Goe Code"
                TYPE_TAXI_STAND -> return "Taxi Stand"
                TYPE_TRAIN_STATION -> return "Train Station"
                TYPE_TRANSIT_STATION -> return "Transit Station"
                TYPE_TRAVEL_AGENCY -> return "Travel Agency"
                TYPE_UNIVERSITY -> return "University"
                TYPE_VETERINARY_CARE -> return "Veterinary Care"
                TYPE_ZOO -> return "Zoo"
                else -> return "Other"
            }
            return "Other"
        }

//        fun getPlaceType(typeId: Int): PlaceTypeModel {
//            for (i in 0 until typeArray?.size!!) {
//                if (typeArray?.get(i)?.id == typeId) {
//                    Log.d("LocationHelper", "getPlaceType ${typeArray?.get(i)?.id} is ${typeArray?.get(i)?.name}")
//                    return typeArray?.get(i)!!
//                }
//            }
//            return PlaceTypeModel("Other", 0)
//        }

        fun getPlaceTypeIndex(placeTypeId: Int): Int {
            for (i in 0 until typeArray?.size!!) {
                if (typeArray?.get(i)?.id == placeTypeId) {
                    Log.d("LocationHelper", "getPlaceType ${typeArray?.get(i)?.id} is ${typeArray?.get(i)?.name}")
                    return i
                }
            }
            return 0
        }

        fun getPlacesTypesArray(): ArrayList<PlaceTypeModel> {
            if (typeArray == null) {
                typeArray = ArrayList()
                typeArray!!.add(PlaceTypeModel("Airport", 2))
                typeArray!!.add(PlaceTypeModel("Amusement Park", 3))
                typeArray!!.add(PlaceTypeModel("Aquarium", 4))
                typeArray!!.add(PlaceTypeModel("Art Gallery", 5))
                typeArray!!.add(PlaceTypeModel("ATM", 6))
                typeArray!!.add(PlaceTypeModel("Bakery", 7))
                typeArray!!.add(PlaceTypeModel("Bank", 8))
                typeArray!!.add(PlaceTypeModel("Bar", 9))
                typeArray!!.add(PlaceTypeModel("Beauty Salon", 10))
                typeArray!!.add(PlaceTypeModel("Bicycle Store", 11))
                typeArray!!.add(PlaceTypeModel("Book Store", 12))
                typeArray!!.add(PlaceTypeModel("Bowling Alley", 13))
                typeArray!!.add(PlaceTypeModel("Bus Station", 14))
                typeArray!!.add(PlaceTypeModel("Cafe", 15))
                typeArray!!.add(PlaceTypeModel("Camp Ground", 16))
                typeArray!!.add(PlaceTypeModel("Car Dealer", 17))
                typeArray!!.add(PlaceTypeModel("Car Rental", 18))
                typeArray!!.add(PlaceTypeModel("Car Repair", 19))
                typeArray!!.add(PlaceTypeModel("Car Wash", 20))
                typeArray!!.add(PlaceTypeModel("Casino", 21))
                typeArray!!.add(PlaceTypeModel("Cemetery", 22))
                typeArray!!.add(PlaceTypeModel("Church", 23))
                typeArray!!.add(PlaceTypeModel("City Hall", 24))
                typeArray!!.add(PlaceTypeModel("Clothing Store", 25))
                typeArray!!.add(PlaceTypeModel("Colloquial Area", 1004))
                typeArray!!.add(PlaceTypeModel("Convenience Store", 26))
                typeArray!!.add(PlaceTypeModel("Country", 1005))
                typeArray!!.add(PlaceTypeModel("Courthouse", 27))
                typeArray!!.add(PlaceTypeModel("Dentist", 28))
                typeArray!!.add(PlaceTypeModel("Department Store", 29))
                typeArray!!.add(PlaceTypeModel("Doctor", 30))
                typeArray!!.add(PlaceTypeModel("Electrician", 31))
                typeArray!!.add(PlaceTypeModel("Electronics Store", 32))
                typeArray!!.add(PlaceTypeModel("Embassy", 33))
                typeArray!!.add(PlaceTypeModel("Establishment", 34))
                typeArray!!.add(PlaceTypeModel("Finance", 35))
                typeArray!!.add(PlaceTypeModel("Fire Station", 36))
                typeArray!!.add(PlaceTypeModel("Floor", 1006))
                typeArray!!.add(PlaceTypeModel("Florist", 37))
                typeArray!!.add(PlaceTypeModel("Food", 38))
                typeArray!!.add(PlaceTypeModel("Funeral Home", 39))
                typeArray!!.add(PlaceTypeModel("Furniture Store", 40))
                typeArray!!.add(PlaceTypeModel("Gas Station", 41))
                typeArray!!.add(PlaceTypeModel("General Contractor", 42))
                typeArray!!.add(PlaceTypeModel("GEOCODE", 1007))
                typeArray!!.add(PlaceTypeModel("Grocery or Supermarket", 43))
                typeArray!!.add(PlaceTypeModel("Gym", 44))
                typeArray!!.add(PlaceTypeModel("Hair Care", 45))
                typeArray!!.add(PlaceTypeModel("Hardware Store", 46))
                typeArray!!.add(PlaceTypeModel("Health", 47))
                typeArray!!.add(PlaceTypeModel("Hindu Temple", 48))
                typeArray!!.add(PlaceTypeModel("Home Goods Store", 49))
                typeArray!!.add(PlaceTypeModel("Hospital", 50))
                typeArray!!.add(PlaceTypeModel("Insurance Agency", 51))
                typeArray!!.add(PlaceTypeModel("Intersection", 1008))
                typeArray!!.add(PlaceTypeModel("Jewelry Store", 52))
                typeArray!!.add(PlaceTypeModel("Laundry", 53))
                typeArray!!.add(PlaceTypeModel("Lawyer", 54))
                typeArray!!.add(PlaceTypeModel("Library", 55))
                typeArray!!.add(PlaceTypeModel("Liquor Store", 56))
                typeArray!!.add(PlaceTypeModel("Locality", 1009))
                typeArray!!.add(PlaceTypeModel("Local Government Office", 57))
                typeArray!!.add(PlaceTypeModel("Locksmith", 58))
                typeArray!!.add(PlaceTypeModel("Lodging", 59))
                typeArray!!.add(PlaceTypeModel("Meal Delivery", 60))
                typeArray!!.add(PlaceTypeModel("Meal Takeaway", 61))
                typeArray!!.add(PlaceTypeModel("Mosque", 62))
                typeArray!!.add(PlaceTypeModel("Movie Rental", 63))
                typeArray!!.add(PlaceTypeModel("Movie Theater", 64))
                typeArray!!.add(PlaceTypeModel("Moving Company", 65))
                typeArray!!.add(PlaceTypeModel("Museum", 66))
                typeArray!!.add(PlaceTypeModel("Natural Feature", 1010))
                typeArray!!.add(PlaceTypeModel("Neighborhood", 1011))
                typeArray!!.add(PlaceTypeModel("Night Club", 67))
                typeArray!!.add(PlaceTypeModel("Other", 0))
                typeArray!!.add(PlaceTypeModel("Painter", 68))
                typeArray!!.add(PlaceTypeModel("Park", 69))
                typeArray!!.add(PlaceTypeModel("Parking", 70))
                typeArray!!.add(PlaceTypeModel("Pet Store", 71))
                typeArray!!.add(PlaceTypeModel("Pharmacy", 72))
                typeArray!!.add(PlaceTypeModel("Physiotherapist", 73))
                typeArray!!.add(PlaceTypeModel("Place of Worship", 74))
                typeArray!!.add(PlaceTypeModel("Plumber", 75))
                typeArray!!.add(PlaceTypeModel("Point Of Interest", 1013))
                typeArray!!.add(PlaceTypeModel("Police", 76))
                typeArray!!.add(PlaceTypeModel("Political", 1012))
                typeArray!!.add(PlaceTypeModel("Postal Code", 1015))
                typeArray!!.add(PlaceTypeModel("Postal Code Prefix", 1016))
                typeArray!!.add(PlaceTypeModel("Postal Town", 1017))
                typeArray!!.add(PlaceTypeModel("Post Box", 1014))
                typeArray!!.add(PlaceTypeModel("Post Office", 77))
                typeArray!!.add(PlaceTypeModel("Premise", 1018))
                typeArray!!.add(PlaceTypeModel("Real Estate Agency", 78))
                typeArray!!.add(PlaceTypeModel("Restaurant", 79))
                typeArray!!.add(PlaceTypeModel("Roofing Contractor", 80))
                typeArray!!.add(PlaceTypeModel("Room", 1019))
                typeArray!!.add(PlaceTypeModel("Route", 1020))
                typeArray!!.add(PlaceTypeModel("RV Park", 81))
                typeArray!!.add(PlaceTypeModel("School", 82))
                typeArray!!.add(PlaceTypeModel("Shoe Store", 83))
                typeArray!!.add(PlaceTypeModel("Shopping Mall", 84))
                typeArray!!.add(PlaceTypeModel("Spa", 85))
                typeArray!!.add(PlaceTypeModel("Stadium", 86))
                typeArray!!.add(PlaceTypeModel("Storage", 87))
                typeArray!!.add(PlaceTypeModel("Store", 88))
                typeArray!!.add(PlaceTypeModel("Street Address", 1021))
                typeArray!!.add(PlaceTypeModel("Sublocality", 1022))
                typeArray!!.add(PlaceTypeModel("Sublocality Level 1", 1023))
                typeArray!!.add(PlaceTypeModel("Sublocality Level 2", 1024))
                typeArray!!.add(PlaceTypeModel("Sublocality Level 3", 1025))
                typeArray!!.add(PlaceTypeModel("Sublocality Level 4", 1026))
                typeArray!!.add(PlaceTypeModel("Sublocality Level 5", 1027))
                typeArray!!.add(PlaceTypeModel("Subpremise", 1028))
                typeArray!!.add(PlaceTypeModel("Subway Station", 89))
                typeArray!!.add(PlaceTypeModel("Synagogue", 90))
                typeArray!!.add(PlaceTypeModel("Synthetic Geocode", 1029))
                typeArray!!.add(PlaceTypeModel("Taxi Stand", 91))
                typeArray!!.add(PlaceTypeModel("Train Station", 92))
                typeArray!!.add(PlaceTypeModel("Transit Station", 1030))
                typeArray!!.add(PlaceTypeModel("Travel Agency", 93))
                typeArray!!.add(PlaceTypeModel("University", 94))
                typeArray!!.add(PlaceTypeModel("Veterinary Care", 95))
                typeArray!!.add(PlaceTypeModel("Zoo", 96))
            }
            return typeArray!!
        }
    }
}