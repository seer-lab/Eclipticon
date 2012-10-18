# Description
Eclipticon is a testing and development software tool to help users optimize for concurrency. This tool will be in the form of an Eclipse plugin and will be targeted to a specific group of users that is developers and testers of concurrent software. The goal of the tool is to ease difficulties that arise when using concurrency in the Java programming language.

The user can specify these instrumentation points either manually or automatically. The automatic generation of these test areas will be random, though following some user‐adjustable configurations. The testing will occur by inserting thread delays that will cause the newly instrumented code to explore a larger set of thread interleavings.

[A poster describing Eclipticon](https://github.com/downloads/sqrlab/eclipticon/eclipticon_poster.pdf)

# Installation
This plugin is compatible with Eclipse Juno (4.2). Two approaches for installation are describe below:

## Update Site (Prefered)
1. Within Eclipse go 'Help' then 'Install New Software'.
2. Add Update Site by clicking 'Add...'.
3. Input 'Eclipticon' for name, and '[http://sqrlab.ca/eclipticon-update/](http://sqrlab.ca/eclipticon-update/)' for location.
4. Select the 'Eclipticon' feature and proceed to install by pressing 'Next'.

## From Source (Development)
Utilization/installation of Eclipticon from source for development purposes involves the following:

1. Clone this repository.
2. Import project into Eclipse.
3. Right-click project and select 'Run As' then 'Eclipse Application'.
4. A new instance of Eclipse will load up with Eclipticon load based on the source code.

# Usage
See the [Usage](https://github.com/sqrlab/eclipticon/wiki/Usage) wiki page.

# Contribution
1. Create a new branch (named appropriately based on feature/fix/issue).
2. Add changes to branch along with updated/added tests.
3. Create Pull Request.
