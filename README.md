## Bill Keeper API

This is the API for the Bill Keeper [Android app] (https://github.com/jfidiles/billkeeper-api).
It's a REST API build in Java using Spring framework and Gradle automation tool.  

## Installation

Make sure you have JDK (Java Development Kit) installed and configured on your system.

1. Download/Clone the repository.
2. If you run Unix systems, make sure you have permissions on the folder (777 will surely make it work).
3. Run `./gradlew build` inside the directory where you downloaded the API (`gradle build` if you run Windows).
4. Run `./gradlew run`.
5. Open `http://localhost:9000/` in your browser and you should get "The API is running." message.

## License

Copyright 2015 Jimmy Fidiles

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
