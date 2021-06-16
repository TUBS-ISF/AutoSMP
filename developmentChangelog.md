# Development Changelog for AutoSMP

## 02.06.2021
- Change Name of Mainclass from TWiseParameterSampler to AutoSMP
- Change package structure of JS implementation to de.tubs.cs.isf.AutoSMP.<packages>
- Change the input parameter of Mainclass to take configPath and configName as first parameters
- Refactor comments for properties


## Todo 03.06.2021
[x] implement pathproperty and set defaults
[x] extend config file with implemented path properties
[x] DEFAULT_LOG_DIRECTORY to be created
[x] DEFAULT_CSV_DIRECTORY to be created
[x] create DEFAULT_TEMP_DIRECTORY
[] fix sampling stability calculation

## 03.06.2021
- extend original config.properties from JS
- change path setting in SamplingConfig/readConfigFile to directory properties set by properties file
- remove stability calculation from AutoSMP temporary
- change refreshPaths method in SamplingConfig to use property values

## 09.06.2021
- there must be a sample.csv file present in your temp directory otherwise fide throws an error

## Todos
- Auftrennen der Model Informationen und der sampling-evaluation results
- Remove Author field from evaluation results

## 16.06.2021
- Chvatal algorithm implemented and running
- reading sample size, sample time and coverage works
- storing sample does work, needs re-implementation
- memory analysis needs to be removed from csv files
- evalautaion result csv append does not work, needs to be checked
- validity check does not work --> confusion of cnf formulas
