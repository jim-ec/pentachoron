import 'package:flutter/material.dart';
import 'package:tesserapp/backdrop.dart';
import 'package:tesserapp/back_layer.dart';
import 'package:tesserapp/front_layer.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Scaffold(
        body: Container(
          color: Theme.of(context).primaryColor,
          child: SafeArea(
            child: Backdrop(
              backLayer: BackLayer(),
              frontLayer: FrontLayer(),
            ),
          ),
        ),
      );
}
