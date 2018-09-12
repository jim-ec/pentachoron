import 'package:flutter/material.dart';

ThemeData tesserTheme(final Brightness brightness) => ThemeData(
      primaryColor: (brightness == Brightness.light)
          ? Colors.lime[800]
          : Colors.grey[900],
      toggleableActiveColor: Colors.lime[900],
      accentColor: Colors.lime[800],
      fontFamily: "GeoNMS",
      brightness: brightness,
      primaryTextTheme: TextTheme(
        title: TextStyle(
          fontSize: 24.0,
          letterSpacing: 1.2,
        ),
        body1: TextStyle(
          letterSpacing: 0.8,
          fontWeight: FontWeight.w600,
        ),
        button: TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 1.2,
        ),
      ),
    );
