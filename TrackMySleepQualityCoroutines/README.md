TrackMySleepQuality - Solution Code for 6.2 Coroutines codelab
==============================================================

Solution code for Android Kotlin Fundamentals Codelab 6.2 Coroutines

Introduction
------------

TrackMySleepQuality is an app for recording sleep data for each night. 
You can record a start and stop time, assign a quality rating, and clear the database. 

This app:

* Extends the TrackMySleepQuality app to collect, store, and display data in and from the database. 
* Uses coroutines to run long-running database operations in the background. 
* Uses LiveData to trigger navigation and showing of a snackbar. 
* Uses LiveData to enable and disable buttons.


Pre-requisites
--------------

You need to know:

* Building a basic user interface (UI) for an Android app, 
  using an activity, fragments, and views.
* Navigating between fragments and using Safe Args (a Gradle plugin) 
  to pass data between fragments.
* View models, view-model factories, and LiveData and its observers. 
  These Architecture Components topics are covered in an earlier codelab in this course.
* A basic understanding of SQL databases and the SQLite language.
* How to create a Room database, create a DAO, and define entities, 
  from the previous Room codelab[LINK]. 
* It is helpful if you are familiar with threading and multiprocessing concepts.


Getting Started
---------------

1. Download and run the app.

License
-------

Copyright 2019 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.