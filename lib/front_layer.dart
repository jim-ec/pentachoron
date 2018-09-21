import 'package:flutter/material.dart';
import 'package:tesserapp/app_options.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/pentachoron.dart';
import 'package:tesserapp/geometry/vector.dart';

class FrontLayer extends StatefulWidget {
  @override
  FrontLayerState createState() => FrontLayerState();
}

class FrontLayerState extends State<FrontLayer> {
  var polar = Angle.fromDegrees(-30.0);
  var azimuth = Angle.fromDegrees(30.0);
  var rotationSliderValue = 0.0;
  var translationSliderValue = 0.0;

  @override
  void initState() {
    super.initState();
  }

  static const orbitSensitivity = 0.014;

  @override
  Widget build(final BuildContext context) => Stack(
        children: [
          GestureDetector(
            onPanUpdate: (details) {
              setState(() {
                polar -= Angle.fromRadians(details.delta.dx *
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
              });
            },
            onDoubleTap: () {
              setState(() {
                polar = Angle.fromDegrees(0.0);
                azimuth = Angle.fromDegrees(0.0);
              });
            },
            child: Canvas3d(
                color: Theme.of(context).accentColor,
                lightDirection: Vector.ofZ(1.0),
                outlineColor: Theme.of(context).textTheme.title.color,
                printDrawStats:
                    AppOptions.of(context).printDrawStats.option.value,
                drawStatsStyle: TextStyle(
                  color: Theme.of(context).textTheme.body1.color,
                  fontFamily: "monospace",
                  fontSize: 11.0,
                ),
                modelMatrix: Matrix.chain([
                  Matrix.rotation(
                      RotationPlane.onXQ, Angle.fromTurns(rotationSliderValue)),
                  Matrix.rotation(RotationPlane.onXY, polar),
                  Matrix.rotation(RotationPlane.onYZ, azimuth),
                  Matrix.translation(
                      Vector(0.0, 3.0, 0.0, translationSliderValue)),
                ]),
                drawableBuilder: () {
                  return [Pentachoron.simple()];
                }),
          ),
          Positioned.fill(
            top: null,
            left: 8.0,
            right: 8.0,
            child: Column(
              children: <Widget>[
                Row(
                  children: <Widget>[
                    Flexible(
                      flex: 1,
                      child: Text("Translation"),
                    ),
                    Slider(
                      activeColor: Theme.of(context).accentColor,
                      value: translationSliderValue,
                      min: -3.0,
                      max: 3.0,
                      onChanged: (value) {
                        setState(() {
                          translationSliderValue = value;
                        });
                      },
                    ),
                  ],
                ),
                Row(
                  children: <Widget>[
                    Flexible(
                      flex: 1,
                      child: Text("Rotation"),
                    ),
                    Slider(
                      activeColor: Theme.of(context).accentColor,
                      value: rotationSliderValue,
                      min: 0.0,
                      max: 1.0,
                      onChanged: (value) {
                        setState(() {
                          rotationSliderValue = value;
                        });
                      },
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      );
}
