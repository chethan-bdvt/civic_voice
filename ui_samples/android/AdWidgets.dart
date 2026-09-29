import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

class CivicAdBanner extends StatefulWidget {
  const CivicAdBanner({super.key});

  @override
  State<CivicAdBanner> createState() => _CivicAdBannerState();
}

class _CivicAdBannerState extends State<CivicAdBanner> {
  BannerAd? _bannerAd;

  @override
  void initState() {
    super.initState();
    _bannerAd = BannerAd(
      adUnitId: 'ca-app-pub-3940256099942544/6300978111',
      size: AdSize.banner,
      request: const AdRequest(),
      listener: BannerAdListener(
        onAdLoaded: (_) => setState(() {}),
      ),
    )..load();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 90,
      alignment: Alignment.center,
      child: _bannerAd != null
          ? AdWidget(ad: _bannerAd!)
          : const Text('Loading ad...'),
    );
  }
}

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

class CivicReportScreen extends StatefulWidget {
  const CivicReportScreen({super.key});

  @override
  State<CivicReportScreen> createState() => _CivicReportScreenState();
}

class _CivicReportScreenState extends State<CivicReportScreen> {
  bool _showVideoAd = false;

  void _submitReport() {
    setState(() {
      _showVideoAd = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Civic Voice')),
      body: Stack(
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                const CivicAdBanner(),
                const SizedBox(height: 16),
                Expanded(
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: ElevatedButton(
                        onPressed: _submitReport,
                        child: const Text('Submit report'),
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                const CivicAdBanner(),
              ],
            ),
          ),
          if (_showVideoAd)
            VideoAdOverlay(
              durationSeconds: 8,
              onComplete: () => setState(() => _showVideoAd = false),
            ),
        ],
      ),
    );
  }
}
