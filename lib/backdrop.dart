import 'package:flutter/material.dart';

class Backdrop extends StatefulWidget {
  final Widget backPanel;
  final Widget frontPanel;

  const Backdrop({
    Key key,
    @required this.backPanel,
    @required this.frontPanel,
  }) : super(key: key);

  @override
  _BackdropState createState() => _BackdropState(frontPanel, backPanel);
}

class _BackdropState extends State<Backdrop> {
  final Widget backPanel;
  final Widget frontPanel;

  _BackdropState(this.frontPanel, this.backPanel);

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) {
          final topOffset = constraints.biggest.height - 64.0;
          return Stack(
              children: <Widget>[
                backPanel,
                Positioned.fill(
                  top: topOffset,
                  bottom: -topOffset,
                  child: frontPanel,
                ),
              ],
            );
        },
      );
}
