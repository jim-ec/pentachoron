import 'package:flutter/material.dart';

ThemeData tesserTheme() => ThemeData(
      primarySwatch: Colors.deepOrange,
      accentColor: Colors.white,
      fontFamily: "GeoNMS",
      primaryTextTheme: TextTheme(
        title: TextStyle(
          fontWeight: FontWeight.bold,
          fontSize: 24.0,
        ),
      ),
    );
