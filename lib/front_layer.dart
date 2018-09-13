import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry4d/geometry.dart';
import 'package:tesserapp/geometry4d/polygons.dart';
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
                    final tetrahedron = Tetrahedron([
                      Vector.zero(),
                      Vector.ofX(1.0),
                      Vector.ofY(1.0),
                      Vector.ofZ(1.0),
                    ]);
                    final plane = Plane.fromNormal(
                        a: Vector.ofZ(sliderValue), n: Vector.ofZ(1.0));
                    return polygonsOfTriangle(tetrahedron.intersected(plane));
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
              min: 0.0,
              max: 1.0,
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
