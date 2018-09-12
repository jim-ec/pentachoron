import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  var polar = Angle.fromDegrees(30.0);
  var azimuth = Angle.fromDegrees(20.0);

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
        child: Canvas3d(
          lightDirection: Vector3(0.0, 0.0, 1.0),
          lightSpace: LightSpace.view,
          outlineMode: OutlineMode.occluded,
          outlineColor: Theme.of(context).textTheme.title.color,
          cameraPosition: CameraPosition.fromOrbitEuler(
            distance: 10.0,
            polar: polar,
            azimuth: azimuth,
          ),
          geometries: [
            Geometry(
              translation: Vector3(4.0, 0.0, 0.0),
              color: Theme.of(context).scaffoldBackgroundColor,
              polygons: cube(
                center: Vector3.zero(),
                sideLength: 1.0,
              ),
            ),
            Geometry(
              color: Theme.of(context).accentColor,
              outlined: true,
              polygons: cube(
                center: Vector3.zero(),
                sideLength: 2.0,
              ),
            ),
          ],
        ),
      );
}
