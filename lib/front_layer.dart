import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  var polar = Angle.fromDegrees(-60.0);
  var azimuth = Angle.fromDegrees(30.0);

  @override
  void initState() {
    super.initState();
  }

  static const orbitSensitivity = 0.008;

  @override
  Widget build(final BuildContext context) => GestureDetector(
        onPanUpdate: (details) {
          setState(() {
            polar += Angle.fromRadians(-details.delta.dx *
                (AppOptions.of(context).invertedHorizontalCamera.option.value
                    ? -orbitSensitivity
                    : orbitSensitivity));
            azimuth += Angle.fromRadians(details.delta.dy *
                (AppOptions.of(context).invertedVerticalCamera.option.value
                    ? -orbitSensitivity
                    : orbitSensitivity));
          });
        },
        onDoubleTap: () {
          setState(() {
            polar = Angle.fromDegrees(0.0);
            azimuth = Angle.fromDegrees(0.0);
          });
        },
        child: Canvas3d(
          lightDirection: Vector3(0.0, 0.0, 1.0),
          fov: Angle.fromDegrees(60.0),
          outlineColor: Theme.of(context).textTheme.title.color,
          cameraPosition: CameraPosition.fromOrbitEuler(
            distance: 10.0,
            polar: polar,
            azimuth: azimuth,
          ),
          geometries: [
//            Geometry(
//                color: Theme.of(context).accentColor,
//                outlined: true,
//                polygons: Pentachoron4.simple().baseCell.polygons),
            Geometry(
                color: Theme.of(context).accentColor,
                translation: Vector3(0.5, 0.0, 0.0),
                polygons: [
                  Polygon([
                    Vector3.zero(),
                    Vector3(0.0, 1.0, 0.0),
                    Vector3(1.0, 0.0, 0.0),
                  ])
                ]
//              polygons: pyramid(edgeLength: 0.5, height: 2.0),
                ),
          ],
        ),
      );
}
