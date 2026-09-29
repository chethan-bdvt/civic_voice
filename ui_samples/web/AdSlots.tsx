import React, { useEffect, useState } from 'react';

type AdSlotProps = {
  label?: string;
  width?: number;
  height?: number;
  className?: string;
};

export function AdSlot({
  label = 'Advertisement',
  width = 320,
  height = 100,
  className = '',
}: AdSlotProps) {
  return (
    <div
      className={`ad-slot ${className}`.trim()}
      aria-label={label}
      style={{
        width: `${width}px`,
        height: `${height}px`,
        background: 'linear-gradient(135deg, #f5f7fb, #dfe7f7)',
        border: '1px solid #d3dcef',
        borderRadius: '12px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#45536b',
        fontWeight: 600,
        letterSpacing: '0.02em',
        margin: '12px auto',
        overflow: 'hidden',
      }}
    >
      {label}
    </div>
  );
}

type VideoAdOverlayProps = {
  open: boolean;
  onComplete: () => void;
  durationSeconds?: number;
};

export function VideoAdOverlay({
  open,
  onComplete,
  durationSeconds = 8,
}: VideoAdOverlayProps) {
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
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(10, 16, 28, 0.72)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
        padding: '24px',
      }}
    >
      <div
        style={{
          width: 'min(720px, 100%)',
          minHeight: '420px',
          background: '#fff',
          borderRadius: '18px',
          padding: '24px',
          textAlign: 'center',
          boxShadow: '0 20px 60px rgba(0,0,0,0.25)',
        }}
      >
        <div style={{ fontSize: '24px', fontWeight: 700, marginBottom: 12 }}>
          Sponsored video ad
        </div>

        <div
          style={{
            background: '#eef3ff',
            borderRadius: '12px',
            height: '220px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '18px',
            color: '#3c4f73',
            fontWeight: 700,
          }}
        >
          Video ad player placeholder
        </div>

        <div style={{ fontSize: '18px', marginBottom: 16 }}>
          Please wait {secondsLeft}s to continue
        </div>

        <button
          onClick={onComplete}
          disabled={secondsLeft > 0}
          style={{
            background: secondsLeft > 0 ? '#b7c4dc' : '#1f6feb',
            color: '#fff',
            border: 'none',
            borderRadius: '10px',
            padding: '12px 20px',
            fontSize: '16px',
            cursor: secondsLeft > 0 ? 'not-allowed' : 'pointer',
          }}
        >
          Continue
        </button>
      </div>
    </div>
  );
}

export function ReportPageLayout() {
  const [showVideoAd, setShowVideoAd] = useState(false);

  const handleSubmit = () => {
    setShowVideoAd(true);
  };

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', gap: 18, alignItems: 'center' }}>
        <h1>Civic Voice</h1>
        <AdSlot label="Top banner ad" width={728} height={90} />
      </header>

      <main style={{ display: 'grid', gridTemplateColumns: '2fr 320px', gap: 24 }}>
        <section>
          <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
            {/* report form fields */}
            <button type="submit">Submit report</button>
          </form>
        </section>

        <aside>
          <AdSlot label="Right rail ad" width={300} height={250} />
          <AdSlot label="Sidebar ad" width={300} height={250} />
        </aside>
      </main>

      <footer style={{ marginTop: 32, display: 'flex', justifyContent: 'center' }}>
        <AdSlot label="Footer banner ad" width={728} height={90} />
      </footer>

      <VideoAdOverlay open={showVideoAd} onComplete={() => setShowVideoAd(false)} durationSeconds={8} />
    </div>
  );
}
