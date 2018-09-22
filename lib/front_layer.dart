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
  
  static get defaultPolar => Angle.fromDegrees(60.0);
  static get defaultAzimuth => Angle.fromDegrees(10.0);
  
  var polar = defaultPolar;
  var azimuth = defaultAzimuth;
  var rotation = Angle.zero();
  var translation = 0.0;

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
                polar = defaultPolar;
                azimuth = defaultAzimuth;
                rotation = Angle.zero();
                translation = 0.0;
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
                  height: 0.8,
                ),
                modelMatrix: Matrix.chain([
                  Matrix.rotation(RotationPlane.onXQ, rotation),
                  Matrix.rotation(RotationPlane.onXY, polar),
                  Matrix.rotation(RotationPlane.onYZ, azimuth),
                  Matrix.translation(
                      Vector(0.0, 3.0, 0.0, translation)),
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
                    Expanded(
                      flex: 1,
                      child: Text("Translation"),
                    ),
                    Expanded(
                      flex: 4,
                      child: Slider(
                        activeColor: Theme.of(context).accentColor,
                        value: translation,
                        min: -2.0,
                        max: 2.0,
                        onChanged: (value) {
                          setState(() {
                            translation = value;
                          });
                        },
                      ),
                    ),
                    Expanded(
                      flex: 1,
                      child: Text(
                        translation.toStringAsFixed(1),
                        textAlign: TextAlign.end,
                      ),
                    ),
                  ],
                ),
                Row(
                  children: <Widget>[
                    Expanded(
                      flex: 1,
                      child: Text("Rotation"),
                    ),
                    Expanded(
                      flex: 4,
                      child: Slider(
                        activeColor: Theme.of(context).accentColor,
                        value: rotation.turns,
                        min: 0.0,
                        max: 1.0,
                        onChanged: (value) {
                          setState(() {
                            rotation = Angle.fromTurns(value);
                          });
                        },
                      ),
                    ),
                    Expanded(
                      flex: 1,
                      child: Text(
                        rotation.toString(),
                        textAlign: TextAlign.end,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      );
}
