# The ModeShape Tools project

## Summary

This is the official Git repository for the open source _ModeShape Tools_ project.

_ModeShape Tools_ is a set of Eclipse plugins for working with [ModeShape](http://www.jboss.org/modeshape) and Java Content Repositories (JCRs). _ModeShape Tools_ is part of the [JBoss Tools](http://www.jboss.org/tools) community of projects.

The current _ModeShape Tools_ features include:

- the ability to publish/upload resources from Eclipse workspaces to [ModeShape](http://www.jboss.org/modeshape) repositories, and
- the ability to create and edit Compact Node Type Definition (CND) files using the included CND form-based editor. The CND editor does not require a connection to a ModeShape or any other JCR repository.

## Install

_ModeShape Tools_ is installed into Eclipse by following these steps:

1. Start up Eclipse 3.7
1. Select `Help > Install New Software… > Add...`
1. In the `Add Repository` dialog, enter a name, for instance "ModeShape Tools Update-Site" and enter this URL, TBD, for the location. Then select `OK` to close the dialog.
1. After the update-site loads select one or more of the _ModeShape Tools_ features you want to install.

## Get the code

The easiest way to get started with the code is to [create your own fork](http://help.github.com/forking/) of this repository, and then clone your fork:

  $ git clone git@github.com:<you>/modeshape-tools.git
	$ cd modeshape-tools
	$ git remote add upstream git://github.com/ModeShape/modeshape-tools.git
	
At any time, you can pull changes from the upstream and merge them onto your master:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the 'upstream/master'.

## Building ModeShape Tools

To build _ModeShape Tools_ requires specific versions of Java and Maven. Also, there is some Maven and Eclipse target platform setup. The [How to Build JBoss Tools with Maven 3](https://community.jboss.org/wiki/HowToBuildJBossToolsWithMaven3#OR_use_MavenAnt_to_get_it) document will guide you through that setup. Specifically, perform these steps this document identifies:

1. adding the `jboss-default` profile to your Maven `settings.xml` file,
1. setting your `MAVEN_OPTS` environment variable, and
1. downloading and unpacking the latest _JBoss Tools_ target platform and using as the `local.site` in your build command.

Once Maven and the Eclipse target platform are setup, this command will run the build:

`mvn clean install -P local.site -Dlocal.site=file:/path/to/unpacked/target-platform/ -Pjbosstools-staging-aggregate`

If you want, your builds can skip the tests by adding this parameter to the above build command: `-Dmaven.test.skip=true`. *But always run the tests before any commits.*

## Contribute fixes and features

_ModeShape Tools_ is open source, and we welcome anybody that wants to participate and contribute!

If you want to fix a bug or make any changes, please log an issue in the [ModeShape JIRA](https://issues.jboss.org/browse/MODE) describing the bug or new feature and give it a component type of `Tools`. Then we highly recommend making the changes on a topic branch named with the JIRA issue number. For example, this command creates a branch for the MODE-1234 issue:

	$ git checkout -b mode-1234

After you're happy with your changes and a full build (with unit tests) runs successfully, commit your changes on your topic branch (using [really good comments](http://community.jboss.org/wiki/ModeShapeDevelopmentGuidelines#Commits)). Then it's time to check for any recent changes that were made in the official repository:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git checkout mode-1234            # switches to your topic branch
	$ git rebase master                 # reapplies your changes on top of the latest in master
	                                      (i.e., the latest from master will be the new base for your changes)

If the pull grabbed a lot of changes, you should rerun your build to make sure your changes are still good. You can then either [create patches](http://progit.org/book/ch5-2.html) (one file per commit, saved in `~/mode-1234`) with 

	$ git format-patch -M -o ~/mode-1234 origin/master

and upload them to the JIRA issue, or you can push your topic branch and its changes into your public fork repository

	$ git push origin mode-1234         # pushes your topic branch into your public fork of ModeShape

and [generate a pull-request](http://help.github.com/pull-requests/) for your changes. 

We prefer pull-requests, because we can review the proposed changes, comment on them, discuss them with you, and likely merge the changes right into the official repository.