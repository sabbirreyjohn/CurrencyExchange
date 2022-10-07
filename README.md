# CurrencyExchange
A currency exchange application

Here are the requirements status:

1. Task done in Kotlin.
2. Jetpack Compose, Dagger Hilt, Retrofit etc libraries were used
3. Here are the overall info of the task
    - MVVM architectural patter has been used
    - Jetpack compose is used for the UI design
    - Dagger Hilt dependency injection used
    - Retrofit used as a networking library
    - Room Pertistance library is used for local database
    - Basic unit testing(database, viewmodel) is done, please check test packages
    
4. Apps current nature and behaviour:
   - App uses api from https://api.apilayer.com
   - Basic UI and views were used just to make this app work. Priorities were given to the functionalities rather than the look.
   - First 5 conversion is free (below amount 200) and then charges 0.7% commission of the sell amount
   - Any conversion more than 200 amount will be charged 0.7% commission.
   - Balance doesn't become negative after conversion
   - Balances(Every currencies that were converted to) is visible in the app
   - Commission rate, number of free conversion etc can be tuned in Constants.KT file
   - PLEASE NOTE:
         1. The periodic sync in every 5 seconds is extended to 1 minute because the api documentation says it will not update
         the currencies before that minimum time.
         2. The API is not free, it might be free before but now it has moved to another domain and requires subscription.
         3. The 1 minute sync is commented out in viewmodel layer to limit the request otherwise it might cross the monthly limit 
            and fail to get response. Feel free to uncomment those lines to check the functionality.
         4. Timer is used to get the periodic sync, but WorkManager could be used for better performance. Just to keep it simple 
            WorkManager is skipped this time.

THANK YOU
