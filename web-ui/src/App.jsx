import React, { useEffect, useMemo, useState } from 'react';

function AdSlot({ label = 'Advertisement', width = 320, height = 90, className = '', animated = false }) {
  return (
    <div
      className={`ad-slot ${animated ? 'ad-animated' : ''} ${className}`.trim()}
      aria-label={label}
      style={{ width, height }}
    >
      {label}
    </div>
  );
}

function InlineAd({ width = 728, height = 90, label = 'Sponsored Content' }) {
  return (
    <div
      className="inline-ad"
      style={{ width: '100%', maxWidth: width, height }}
    >
      <span className="sponsored-badge">Sponsored</span>
      {label}
    </div>
  );
}

function DownloadAdOverlay({ open, onComplete, durationSeconds = 5 }) {
  const [secondsLeft, setSecondsLeft] = useState(durationSeconds);

  useEffect(() => {
    if (!open) return;

    setSecondsLeft(durationSeconds);
    const timer = window.setInterval(() => {
      setSecondsLeft((prev) => {
        if (prev <= 1) {
          window.clearInterval(timer);
          onComplete();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => window.clearInterval(timer);
  }, [open, durationSeconds, onComplete]);

  if (!open) return null;

  return (
    <div className="video-ad-backdrop">
      <div className="video-ad-card">
        <h3>Thank you for watching our sponsor</h3>
        <div className="video-ad-player">Premium Sponsor Ad - Video Placeholder</div>
        <p>Your download will be ready in {secondsLeft}s</p>
        <button onClick={onComplete} disabled={secondsLeft > 0}>
          {secondsLeft > 0 ? `Wait ${secondsLeft}s` : 'Download Now'}
        </button>
      </div>
    </div>
  );
}

function LoginModal({ open, onLogin, stateOptions, districtsByState }) {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [state, setState] = useState('Maharashtra');
  const [district, setDistrict] = useState('Pune');

  const handleGoogleLogin = () => {
    if (email && name) {
      onLogin({ email, name, state, district });
      setEmail('');
      setName('');
    }
  };

  if (!open) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal-card">
        <h2>Login to Download</h2>
        <div className="login-form">
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Full Name"
            required
          />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email Address"
            required
          />
          <select
            value={state}
            onChange={(e) => {
              setState(e.target.value);
              setDistrict(districtsByState[e.target.value][0]);
            }}
          >
            {stateOptions.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
          <select
            value={district}
            onChange={(e) => setDistrict(e.target.value)}
          >
            {districtsByState[state].map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
          <button onClick={handleGoogleLogin} className="google-login-btn">
            🔐 Login with Google & Proceed
          </button>
        </div>
      </div>
    </div>
  );
}

export default function App() {
  const stateOptions = ['Maharashtra', 'Delhi', 'Karnataka', 'Tamil Nadu', 'West Bengal', 'Gujarat'];
  const districtsByState = {
    'Maharashtra': ['Pune', 'Mumbai', 'Nashik', 'Nagpur'],
    'Delhi': ['Central Delhi', 'East Delhi', 'North Delhi', 'South Delhi'],
    'Karnataka': ['Bangalore', 'Mysore', 'Belgaum', 'Mangalore'],
    'Tamil Nadu': ['Chennai', 'Coimbatore', 'Madurai', 'Salem'],
    'West Bengal': ['Kolkata', 'Darjeeling', 'Howrah', 'Jalpaiguri'],
    'Gujarat': ['Ahmedabad', 'Surat', 'Vadodara', 'Rajkot']
  };

  const [reportForm, setReportForm] = useState({
    state: 'Maharashtra',
    district: 'Pune',
    reason: '',
    amount: '',
    description: ''
  });
  const [searchTerm, setSearchTerm] = useState('Pune bribery');
  const [showSearchAd, setShowSearchAd] = useState(false);
  const [reportStatus, setReportStatus] = useState('');
  const [searchStatus, setSearchStatus] = useState('');
  
  // Login & Download states
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showDownloadAd, setShowDownloadAd] = useState(false);
  const [pendingDownload, setPendingDownload] = useState(null);

  const searchResults = useMemo(
    () => [
      { id: 1, title: 'Public Works - Pune', status: 'Unverified allegation' },
      { id: 2, title: 'Urban Development - Nashik', status: 'Under review' },
      { id: 3, title: 'Transport - Mumbai', status: 'Archived' }
    ],
    []
  );

  const handleReportSubmit = (event) => {
    event.preventDefault();
    setReportStatus('Report submitted successfully.');
  };

  const handleSearchSubmit = (event) => {
    event.preventDefault();
    setSearchStatus(`Showing results for "${searchTerm}"`);
    setShowSearchAd(true);
  };

  const handleDownloadClick = (reportId) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
    } else {
      // Show ad before download
      setPendingDownload(reportId);
      setShowDownloadAd(true);
    }
  };

  const handleLogin = (userData) => {
    setUser(userData);
    setIsLoggedIn(true);
    setShowLoginModal(false);
  };

  const handleDownloadComplete = () => {
    if (pendingDownload) {
      const report = searchResults.find((r) => r.id === pendingDownload);
      if (report) {
        // Create CSV data
        const csvData = `Report ID,Title,Status,User,Email,State,District\n${pendingDownload},"${report.title}","${report.status}","${user.name}","${user.email}","${user.state}","${user.district}"`;
        
        // Download as CSV (can easily convert to PDF with a library)
        const blob = new Blob([csvData], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `civic-report-${pendingDownload}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
        
        setPendingDownload(null);
      }
    }
  };

  return (
    <div className="page-shell">
      <header className="top-bar">
        <div className="brand">Civic Voice</div>
        {isLoggedIn && user && (
          <div className="user-profile">
            👤 {user.name} | {user.state}, {user.district}
          </div>
        )}
        <AdSlot label="Top banner ad" width={728} height={90} />
      </header>

      <main className="content-grid">
        <section className="main-panel">
          <div className="panel-card">
            <h2>Submit report</h2>
            <form onSubmit={handleReportSubmit} className="report-form">
              <select
                value={reportForm.state}
                onChange={(e) => {
                  const newState = e.target.value;
                  setReportForm((prev) => ({ 
                    ...prev, 
                    state: newState,
                    district: districtsByState[newState][0]
                  }));
                }}
              >
                {stateOptions.map((state) => (
                  <option key={state} value={state}>{state}</option>
                ))}
              </select>

              <select
                value={reportForm.district}
                onChange={(e) => setReportForm((prev) => ({ ...prev, district: e.target.value }))}
              >
                {districtsByState[reportForm.state].map((dist) => (
                  <option key={dist} value={dist}>{dist}</option>
                ))}
              </select>

              <input
                value={reportForm.reason}
                onChange={(e) => setReportForm((prev) => ({ ...prev, reason: e.target.value }))}
                placeholder="Reason for complaint"
                required
              />

              <input
                type="number"
                value={reportForm.amount}
                onChange={(e) => setReportForm((prev) => ({ ...prev, amount: e.target.value }))}
                placeholder="Amount involved (in INR)"
                min="0"
              />

              <textarea
                rows="4"
                value={reportForm.description}
                onChange={(e) => setReportForm((prev) => ({ ...prev, description: e.target.value }))}
                placeholder="Description of the incident"
              />
              <button type="submit">Submit report</button>
            </form>
            {reportStatus && <p className="status-text">{reportStatus}</p>}
          </div>

          <div className="panel-card">
            <h2>Search reports</h2>
            <form onSubmit={handleSearchSubmit} className="search-form">
              <input
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search by location or department"
              />
              <button type="submit">Search</button>
            </form>
            {searchStatus && <p className="status-text">{searchStatus}</p>}
            
            <InlineAd width={700} height={80} label="🎯 Report corruption anonymously and securely with Civic Voice" />
            
            <div className="results-list">
              {searchResults.map((item, idx) => (
                <React.Fragment key={item.id}>
                  <div className="result-item">
                    <div className="result-info">
                      <strong>{item.title}</strong>
                      <span>{item.status}</span>
                    </div>
                    <div className="result-actions">
                      <button 
                        className="download-btn"
                        onClick={() => handleDownloadClick(item.id)}
                      >
                        ⬇️ {isLoggedIn ? 'Download' : 'Login to Download'}
                      </button>
                    </div>
                  </div>
                  {idx === 1 && <InlineAd width={700} height={80} label="📢 Join 10,000+ citizens reporting corruption" />}
                </React.Fragment>
              ))}
            </div>
          </div>
        </section>

        <aside className="sidebar-panel">
          <div className="sticky-sidebar">
            <AdSlot label="Premium Ad" width={300} height={250} className="sidebar-ad" animated={true} />
            <AdSlot label="Sponsored Content" width={300} height={250} className="sidebar-ad" animated={true} />
            <AdSlot label="Partner Ad" width={300} height={250} className="sidebar-ad" animated={true} />
          </div>
        </aside>
      </main>

      <footer className="footer-bar">
        <AdSlot label="Footer banner ad" width={728} height={90} />
      </footer>

      <VideoAdOverlay
        open={showSearchAd}
        onComplete={() => setShowSearchAd(false)}
        durationSeconds={6}
      />

      <LoginModal 
        open={showLoginModal}
        onLogin={handleLogin}
        stateOptions={stateOptions}
        districtsByState={districtsByState}
      />

      <DownloadAdOverlay
        open={showDownloadAd}
        onComplete={handleDownloadComplete}
        durationSeconds={5}
      />
    </div>
  );
}
