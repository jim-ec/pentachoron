import 'package:flutter/material.dart';

ThemeData tesserTheme() => ThemeData(
      primarySwatch: Colors.deepOrange,
      accentColor: Colors.deepOrangeAccent,
      fontFamily: "GeoNMS",
      brightness: Brightness.dark,
      textTheme: TextTheme(
        body1: TextStyle(
          letterSpacing: 1.2,
        ),
        body2: TextStyle(
          letterSpacing: 0.9,
        ),
        button: TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 1.2,
        ),
      ),
      primaryTextTheme: TextTheme(
        title: TextStyle(
          fontSize: 24.0,
          letterSpacing: 1.2,
        ),
        body1: TextStyle(
          fontWeight: FontWeight.bold,
          letterSpacing: 1.0,
        ),
      ),
    );
