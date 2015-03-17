If you don't want to deal with source code, then please go to [Downloads](http://code.google.com/p/datanaliz/downloads/list) for the jar file. This page describes how to build from the latest sources.

Before starting, please make sure that you have [JDK6](http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u31-download-1501634.html) (see the notes at the end), [Mercurial](http://mercurial.selenic.com/), and [Maven 2](http://maven.apache.org/download.html) in your system.

## Download sources ##

For anonymous pull:

```
hg clone https://code.google.com/p/datanaliz/
```

If you plan to contribute:

```
hg clone https://[username]@code.google.com/p/datanaliz/
```

where `[username`] is your Google username, without brackets.
This will create a directory named `datanaliz`, containing all the sources.

## Compile ##

Go into the project directory, and tell maven to compile the project.

```
cd datanaliz
mvn clean compile
```

## Prepare Jar ##

```
mvn assembly:single
```

This will create `datanaliz.jar` under the `target` folder in the project directory.

## Run datanaliz ##

Datanaliz project is meant to be used by programmers, but it also provides few functions for end-users. For instance you can visualize the distribution of gene expressions of specific genes in a specific GEO dataset using the following commands.

Go to the location of jar file.

```
cd target
```

Run datanaliz with the following command.

```
java -jar datanaliz.jar expdist GSE17913 CYP1A1 CYP1B1
```

This will download the dataset GSE17913, and display the expression distribution of genes CYP1A1 and CYP1B1. You can try this with different GEO series IDs (GSE.....), and with different genes (has to be official gene symbols, one or more).

Alternatively, you can write "CCLEExpData" instead of GEO series ID, for loading CCLE expression dataset.

```
java -jar datanaliz.jar expdist CCLEExpData CCND1 CAV1 FN1
```

You can also make the program read gene names from a text file using `-f` option.

```
java -jar datanaliz.jar expdist CCLEExpData -f pathToFile.txt
```

## Contributing ##

You can commit your changes, while in the project directory (`datanaliz`), with:

```
hg commit
```

This will let you write a commit message, and will commit your changes to your local repository.

If you are a committer to the project, you can push your committed changes to Google repository with:

```
hg push
```

## Notes ##

  * JDK7 and Maven2 do not work well together. That's why we recommend using JDK6.
  * Check [this](http://cbio.mskcc.org/~ozgun/) site for other updates.