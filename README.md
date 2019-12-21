# RxBilling
[![Build status](https://api.travis-ci.org/prcaen/rxbilling.svg?branch=master)](https://travis-ci.org/prcaen/rxbilling) [ ![JCenter](https://api.bintray.com/packages/prcaen/maven/rxbilling/images/download.svg) ](https://bintray.com/prcaen/maven/rxbilling/_latestVersion)

RxJava binding APIs for Google Play Billing.

## API documentation
Available soon.

## Download

```groovy
implementation 'fr.prcaen.rxbilling:0.0.1'
```

## Contributes

### Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/prcaen/rxbilling/issues).

### Build

To build:

```
git clone git@github.com:prcaen/rxbilling.git
cd rxbilling/
./gradlew build
```

### Deploy

#### Release

1. Bump version code in `gradle.properties` 
2. Launch the following command: `./gradlew rxbilling:clean rxbilling:testReleaseUnitTest rxbilling:artifactoryPublish`

### Check for dependencies updates

```
./gradlew dependencyUpdates
```

## License

    Copyright (c) 2016-present, Pierrick Caen (https://pierrickcaen.fr).

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
