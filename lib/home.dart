import 'package:flutter/material.dart';
import 'package:pentachoron/back_layer.dart';
import 'package:pentachoron/backdrop/backdrop.dart';
import 'package:pentachoron/front_layer.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
}
