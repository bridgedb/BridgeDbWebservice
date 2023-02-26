[![DOI](https://zenodo.org/badge/546656929.svg)](https://zenodo.org/badge/latestdoi/546656929)
[![build](https://github.com/bridgedb/BridgeDbWebservice/actions/workflows/build.yml/badge.svg)](https://github.com/bridgedb/BridgeDbWebservice/actions/workflows/build.yml)

# BridgeDb Webservice 2.x

The BridgeDb Webservice provides a REST service to access identifier mapping data. It uses the [BridgeDb Java library](https://github.com/bridgedb/bridgedb) and RESTlet technologies to make the webservice available. 

## Running the webservice

Use the latest jar file available here: [link](https://github.com/hbasaric/BridgeDbWebservice/releases/download/2.0/bridgedb-webservice-2.0.0.jar)

### Downloading BridgeDb ID mapping databases

BridgeDb ID mapping databases are found on [this](https://bridgedb.github.io/data/gene_database/) website. 

These derby databases are used to perform identifier mapping for a particular species.

The location of the downloaded files is needed for the below described gdb.config file.

### Modifying the gdb.config file

The gdb.config ("gene database configuration") file in the repository should be customized to point to each local derby file you have downloaded. 

For example, if you are only interested in mapping identifiers for Homo sapiens, your gdb.config file would look like:

```bash
Homo sapiens C:/.../Path_To_Derby_Database/Hs_Derby_Ensembl_XXX.bridge
```

where "Homo sapiens" and the path to the derby database are separated by a tab.
 

### Starting the webservice

When starting the webservice, the gdb.config file will be parsed to determine which gene databases you have configured.

Navigating to the folder where you saved the jar file and run the following line:

```bash
java -jar target/BridgeDbWebservice-*-jar-with-dependencies.jar
```

### Testing an endpoint

Point your browser to: http://localhost:8080/Human/attributeSet

Please see [Swagger Documentation](https://bridgedb.github.io/swagger/#/Identifiers%20(Genes%2C%20proteins%2C%20metabolites%2C%20interactions)/get__organism__properties) for your reference to see which endpoints are available for you to use.
