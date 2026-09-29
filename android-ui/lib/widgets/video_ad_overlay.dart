import 'dart:async';
import 'package:flutter/material.dart';

class VideoAdOverlay extends StatefulWidget {
  final VoidCallback onComplete;
  final int durationSeconds;

  const VideoAdOverlay({
    super.key,
    required this.onComplete,
    this.durationSeconds = 8,
  });

  @override
  State<VideoAdOverlay> createState() => _VideoAdOverlayState();
}

class _VideoAdOverlayState extends State<VideoAdOverlay> {
  late int _secondsLeft;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _secondsLeft = widget.durationSeconds;
    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      if (!mounted) return;
      setState(() {
        _secondsLeft--;
      });

      if (_secondsLeft <= 0) {
        _timer?.cancel();
        widget.onComplete();
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Positioned.fill(
      child: Container(
        color: Colors.black.withOpacity(0.7),
        alignment: Alignment.center,
        child: Container(
          width: 360,
          padding: const EdgeInsets.all(24),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(18),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text(
                'Sponsored video ad',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 18),
              Container(
                height: 180,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(12),
                  color: Colors.blue.shade50,
                ),
                alignment: Alignment.center,
                child: const Text('Video ad player placeholder'),
              ),
              const SizedBox(height: 18),
              Text('Please wait ${_secondsLeft}s to continue'),
              const SizedBox(height: 18),
              ElevatedButton(
                onPressed: _secondsLeft <= 0 ? widget.onComplete : null,
                child: const Text('Continue'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
