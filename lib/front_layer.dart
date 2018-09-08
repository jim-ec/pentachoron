import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  var polar = Angle.zero();
  var azimuth = Angle.zero();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(final BuildContext context) => GestureDetector(
        onPanUpdate: (details) {
          setState(() {
            polar += Angle.fromRadians(details.delta.dx * 0.01);
            azimuth += Angle.fromRadians(-details.delta.dy * 0.01);
          });
        },
        child: Canvas3d(
          parameters: DrawParameters(
            antiAliasing: true,
            outlineMode: OutlineMode.occluded,
            outlineColor: Theme.of(context).accentColor,
            cameraPosition: CameraPosition.fromOrbitEuler(
              distance: 10.0,
              polar: Angle.fromDegrees(10.0),
              azimuth: Angle.fromDegrees(20.0),
            ),
            geometries: [
              Geometry(
                translation: Vector3(3.0, 0.0, 0.0),
                color: Theme.of(context).scaffoldBackgroundColor,
                polygons: cube(
                  center: Vector3.zero(),
                  sideLength: 1.0,
                ),
              ),
              Geometry(
                culling: CullMode.off,
                rotation: Rotation.fromEuler(polar, azimuth, Angle.zero()),
                color: Theme.of(context).primaryColor.withOpacity(0.6),
                outlined: true,
                polygons: cube(
                  center: Vector3.zero(),
                  sideLength: 2.0,
                ),
              ),
            ],
          ),
        ),
      );
}
