# AutoSMP

This repository contains the source code and executable jar file for the submission of AutoSMP at SPLC21 demonstration and tool track.

## Content
This repository cotains the following data:

###[algorithms](https://github.com/TUBS-ISF/AutoSMP/tree/main/algorithms)
This folder stores the executable sampling algorithms and the interface implementations to call the sampling algorithms with AutoSMP.

###[benchmarks](https://github.com/TUBS-ISF/AutoSMP/tree/main/benchmarks)
Contains the benchmarks to evaluate sampling algorithms. The benchmarcks currently contained in this repository are collections of variability models in the FeatureIDE-XML-Format. We classified the variability model by size. To save disc space and download rates the benchmarks are archived. Make sure to unpack the required archives before using AutoSMP.

###[configs](https://github.com/TUBS-ISF/AutoSMP/tree/main/configs)
This folder contains system specific configuration files to define the run-time behaviour and basic input and output paths of AutoSMP.

###[custom-evaluation-results](https://github.com/TUBS-ISF/AutoSMP/tree/main/custom-evaluation-results)
With AutoSMP customized evaluation tables can  be calculated. The customized evaluation tables are stored in this folder.

###[executable](https://github.com/TUBS-ISF/AutoSMP/tree/main/executable)
This folder contains the executable jar file of AutoSMP.

###[libraries](https://github.com/TUBS-ISF/AutoSMP/tree/main/libraries)
Contians a collection of java labraries needed by AutoSMP. This folder is only interesting for those who plan to work with the source code of AutoSMP directly.

###[output](https://github.com/TUBS-ISF/AutoSMP/tree/main/output)
A folder for temporary output files and log files.

###[request](https://github.com/TUBS-ISF/AutoSMP/tree/main/request)
This folder contains the configuration of user requests. A user request tells AutoSMP how to calculate a recommendation table based on previously calculated sample evaluations. User can specify which benchmarks will be considered and how different evaluation criteria for sampling algorithms will be considered.

###[sampling-evaluation-results](https://github.com/TUBS-ISF/AutoSMP/tree/main/sampling-evaluation-results)
This folder contains the results of evaluations performed with AutoSMP. The results are stored as table representation in the CSV file format.

###[source-code](https://github.com/TUBS-ISF/AutoSMP/tree/main/source-code)
This is the folder where the source-code of AutoSMP is stored. Currently this folder contains the following projects:
- [AutoSMP](https://github.com/TUBS-ISF/AutoSMP/tree/main/source-code/AutoSMP)
The source code of our platform AutoSMP
- [AlgorithmExample](https://github.com/TUBS-ISF/AutoSMP/tree/main/source-code/AlgorithmExample/src)
Contains source code of an example implementation to test the registration of algorithms.

## How to use AutoSMP
1. Clone the repository of AutoSMP to any directory on your local system.
2. Decide on which of the registered algorithms you want to evaluate.
3. Decide which benchmarks should be used for the evaluation. Make sure to unpack the required benchmarks.
4. Create a configuration file in the [configs](https://github.com/TUBS-ISF/AutoSMP/tree/main/configs) folder for your system and requirements. A simple way to do this, is to copy the existing [example configuration](https://github.com/TUBS-ISF/AutoSMP/blob/main/configs/exampleConfig.properties) and adjust the file paths and runtime switches to suite your needs.
5. Create a request configuration file in the [request](https://github.com/TUBS-ISF/AutoSMP/tree/main/request) folder to configure how AutoSMP calculate a customized recommendation table. A simple way to do this is by copying and adjusting the existing [example request](https://github.com/TUBS-ISF/AutoSMP/blob/main/request/exampleRequest.properties) file.
6. Switch to the [executable](https://github.com/TUBS-ISF/AutoSMP/tree/main/executable) folder and open a terminal.
7. Execute AutoSMP according to the following command structure:
java -jar ./autoSMP.jar <path to configuration folder> <name of configuration (without extension)> <-sampling> <-request> <path to the request configuration file>
The <-sampling>, <-request>, and <path to request configuration file> are optional switches you can use to specify which analysis AutoSMP will perform.
The *-sampling* switch tells AutoSMP to perform a sample evaluation according to the configuration specified in your configuration file.
The *-request* switch tells AutoSMP to calculate a recommendation table based on your request configuration.  
The <path to request configuration file> tells AutoSMP where to find your request configuration. This argument must be provided if you use the *-request* argument. Otherwise it must not appear in the command.
