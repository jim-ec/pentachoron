import 'package:flutter/material.dart';
import 'package:tesserapp/canvas_4d.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  CameraPosition cameraPosition;

  @override
  void initState() {
    super.initState();
    cameraPosition = CameraPosition(distance: 10.0);
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
          cameraPosition: cameraPosition,
          geometries: [
            Geometry(
              translation: null,
              quaternion: null,
              scale: null,
              color: Theme
                  .of(context)
                  .accentColor,
              faces: cube(
                center: Vector3.zero(),
                sideLength: 2.0,
              ),
            ),
            Geometry(
              translation: null,
              quaternion: null,
              scale: null,
              color: Theme
                  .of(context)
                  .primaryColor,
              faces: cube(
                center: Vector3(3.0, 0.0, 0.0),
                sideLength: 1.0,
              ),
            ),
          ],
        ),
      );
}
