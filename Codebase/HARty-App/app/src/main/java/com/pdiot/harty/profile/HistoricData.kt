package com.pdiot.harty.profile

/* This Kotlin data class allows us to create an instance of a historic data set. */
data class HistoricData(var date : String ?= null, var steps : String ?= null, var sittingTime : Int ?= null, var standingTime : Int ?= null, var runningTime : Int ?= null, var walkingTime : Int ?= null, var lyingTime : Int ?= null, var stairsTime : Int ?= null, var generalTime : Int ?= null, var sittingStandingTime : Int ?= null)
