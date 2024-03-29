# Currency Converter

## Description

This small project uses the specified Revolut API to obtain currency conversion rates from a specific currency to a variety of others. The required UI dictates that these conversion rates are applied to an input value and displayed in a list as numerical values along with the associated currency codes, currency names and national flags.
 
The application therefore hits the Revolut API to obtain the conversion rates for the Euro. Due to the fact that this API only provides the currency codes and conversion rates, the application then uses the RESTCountries API to obtain a list of countries where each currency is recognised. This list is evaluated to identify the country with the largest population and the flag asset for this country is then loaded into list along with the currencies name.

When another currency is selected, the Revolut API is then used to obtain the conversion rates for that currency.

## Known Issues

* The resouce for the EU flag has been included within the project and the CurrencyApadter does a horrible string comparision with the provided URL and uses the EU flag resouce if the URL matches the REST countires API German flag URL. This URL is provided to the CurrencyAdapater because Germany is the country with the largest population that uses the Euro currency. 

## Architecture

The application uses a MVVM architecture and tries to focus on clean architecture principles to ensure that the dependencies within the application are correctly orientated and that code based remains changeable. The diagram below illustrates the main classes within the application and their primary dependencies. 

![alt text](./images/ArchitectureDiagram.svg "ArchitectureDiagram")

## Responsibilities

### CurrencyFragment & CurrencyAdapter
Aware of Android framework and responsible for presenting view model state to the user.

### CurrencyViewModel
Reacting to user inputs and to translate state from use case into an appropriate form for visual display.

### CurrencyUseCase
To control both meta data and conversion data sources and to combine their states to describe the associtated information of known currencies.

### ConversionDataSource
Store and distribute the current state of the known conversion rates.

### ConversionController
Aware of Revolut API and aims to control network calls and to automatically control refresh rate. Also supplies results to data source.

### ConversionNetworkApi
Tied to specific implementation of networking and performs network calls.

### MetaDataDataSource
Store and distribute the current state of the known meat data for requested currency codes.

### MetaDataController
Aware of RESTCounties API and aims to control network calls and provide results back to data source. Also responsible for evaluating countries by population size and identifying the most appropriate flag URLs.

### MetaDataNetworkApi
Tied to specific implementation of networking and performs network calls.

## Model Flow

Each application layer passes the most appropriate data entities across each architecture boundary. While this results in a large number of similar data classes, it aids decoupling each layer from one another. Mappers are used to perform the transformations from one data entity to another. The diagram below describes the data model flows within the application.

![alt text](./images/ModelFlow.svg "Model Flow Diagram")

## Assumptions

* Specification states that the app should refresh every second. The implementation does not simply send a request every second but rather waits until a (un)successful response, delays a second and then repeats. This avoids sending multiple requests when it may not be necessary i.e. when there is no data connection.

* Demo video dipicts inputs numbers without decimal values. This project has replicated this behaviour.

## Limitations

The following items describes limitations of the application that could be improved if development was continued.

* The application performs no data connection checking

* The application performs no specific threading but rather relies on the behaviour of LiveData and Retrofit libraries

* The application performs no persistance

* The application does not include a bespoke app icon

* The application obtains the meta data for each currency uses a queue, the introduction of a factory class would allow for this to be done concurrently
