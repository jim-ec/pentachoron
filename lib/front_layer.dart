import 'package:flutter/material.dart';
import 'package:tesserapp/angle.dart';
import 'package:tesserapp/canvas_4d.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  CameraPosition cameraPosition;
  
  @override
  void initState() {
    super.initState();
    cameraPosition =
        CameraPosition(distance: 5.0);
  }

  @override
  Widget build(final BuildContext context) => GestureDetector(
        onPanUpdate: (details) {
          setState(() {
            cameraPosition.polar = cameraPosition.polar +
                Angle.fromRadians(-details.delta.dx * 0.01);
            cameraPosition.azimuth = cameraPosition.azimuth +
                Angle.fromRadians(details.delta.dy * 0.01);
          });
        },
        child: Canvas4d(
        color: Theme.of(context).accentColor,
          cameraPosition: cameraPosition,
        ),
      );
}
