import 'package:flutter/material.dart';
import 'package:tesserapp/common.dart';

AppBar appBar() => AppBar(
      elevation: 0.0,
      title: stack([
        Positioned.fill(
          child: center(
            Text("TESSERAPP"),
          ),
        ),
        Positioned.fill(
          child: Icon(Icons.keyboard_arrow_down),
          left: null,
        )
      ]),
    );
