import 'package:flutter/material.dart';

class TopBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Stack(
        children: <Widget>[
          Positioned.fill(
            child: Center(
              child: Text(
                "TESSERAPP",
                style: Theme.of(context).primaryTextTheme.title,
              ),
            ),
          ),
          Positioned.fill(
            child: Icon(Icons.keyboard_arrow_down),
            left: null,
          )
        ],
      );
}
