import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/tetrahedron.dart';
import 'package:tesserapp/geometry/vector.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  var polar = Angle.fromDegrees(-30.0);
  var azimuth = Angle.fromDegrees(30.0);
  var sliderValue = 0.0;

  @override
  void initState() {
    super.initState();
  }

  static const orbitSensitivity = 0.008;

  @override
  Widget build(final BuildContext context) => Stack(
        children: [
          GestureDetector(
            onPanUpdate: (details) {
              setState(() {
                polar += Angle.fromRadians(details.delta.dx *
                    (AppOptions.of(context)
                            .invertedHorizontalCamera
                            .option
                            .value
                        ? -orbitSensitivity
                        : orbitSensitivity));
                azimuth += Angle.fromRadians(details.delta.dy *
                    (AppOptions.of(context).invertedVerticalCamera.option.value
                        ? -orbitSensitivity
                        : orbitSensitivity));
                azimuth = Angle.fromDegrees(azimuth.degrees.clamp(-80.0, 80.0));
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
                distance: 5.0,
                polar: polar,
                azimuth: azimuth,
              ),
              geometries: <Geometry>[
                Geometry(
                  color: Theme.of(context).accentColor,
                  polygons: (() {
                    final m = Matrix.chain([
                      Matrix.rotation(
                          RotationPlane.aroundZ, Angle.fromDegrees(45.0)),
//                      Matrix.rotation(
//                          RotationPlane.aroundY, Angle.fromDegrees(45.0)),
//                      Matrix.rotation(
//                          RotationPlane.aroundX, Angle.fromDegrees(-45.0)),
                      Matrix.translation(Vector.ofZ(sliderValue))
                    ]);
                    final tetrahedron = Tetrahedron(m.transformAll([
                      Vector(0.0, -1.0, 1.0),
                      Vector(1.0, 1.0, -1.0),
                      Vector(-1.0, 1.0, -1.0),
                      Vector(0.0, 2.0, 2.0)
                    ]));

//                    return Polygon.tetrahedron(tetrahedron);
                    return [tetrahedron.intersection(0)];
                  })(),
                ),
              ]
//              + axisIndicator
                  ,
            ),
          ),
          Positioned.fill(
            top: null,
            child: Slider(
              value: sliderValue,
              min: -2.0,
              max: 2.0,
              onChanged: (value) {
                setState(() {
                  sliderValue = value;
                });
              },
            ),
          )
        ],
      );
}
