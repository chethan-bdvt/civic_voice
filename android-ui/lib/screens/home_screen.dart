import 'package:flutter/material.dart';
import 'package:civic_voice_android/widgets/ad_banner.dart';
import 'package:civic_voice_android/widgets/video_ad_overlay.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final TextEditingController _searchController = TextEditingController(text: 'Pune bribery');
  bool _showReportAd = false;
  bool _showSearchAd = false;

  void _submitReport() {
    setState(() {
      _showReportAd = true;
    });
  }

  void _submitSearch() {
    setState(() {
      _showSearchAd = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Civic Voice'),
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const AdBanner(adUnitId: 'ca-app-pub-3940256099942544/6300978111'),
                const SizedBox(height: 16),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Submit report',
                          style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 16),
                        TextFormField(
                          initialValue: 'Bribery complaint',
                          decoration: const InputDecoration(labelText: 'Title'),
                        ),
                        const SizedBox(height: 12),
                        TextFormField(
                          initialValue: 'Maharashtra',
                          decoration: const InputDecoration(labelText: 'State'),
                        ),
                        const SizedBox(height: 12),
                        TextFormField(
                          initialValue: 'Public Works',
                          decoration: const InputDecoration(labelText: 'Department'),
                        ),
                        const SizedBox(height: 12),
                        TextFormField(
                          initialValue: 'Requesting support for a misconduct allegation.',
                          maxLines: 4,
                          decoration: const InputDecoration(labelText: 'Description'),
                        ),
                        const SizedBox(height: 16),
                        ElevatedButton(
                          onPressed: _submitReport,
                          child: const Text('Submit report'),
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Search reports',
                          style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 12),
                        TextField(
                          controller: _searchController,
                          decoration: const InputDecoration(labelText: 'Search query'),
                        ),
                        const SizedBox(height: 12),
                        ElevatedButton(
                          onPressed: _submitSearch,
                          child: const Text('Search'),
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                const AdBanner(adUnitId: 'ca-app-pub-3940256099942544/6300978111'),
              ],
            ),
          ),
          if (_showReportAd)
            VideoAdOverlay(
              durationSeconds: 8,
              onComplete: () => setState(() => _showReportAd = false),
            ),
          if (_showSearchAd)
            VideoAdOverlay(
              durationSeconds: 6,
              onComplete: () => setState(() => _showSearchAd = false),
            ),
        ],
      ),
    );
  }
}
