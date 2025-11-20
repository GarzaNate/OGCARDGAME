import './globals.css';
import { UserProvider } from "../context/UserContext";


export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <UserProvider>
          <div className="app-shell min-h-screen flex flex-col">
            <header className="app-header bg-transparent py-4 px-6 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="logo w-10 h-10 rounded-md bg-white/10 flex items-center justify-center text-xl font-bold">♠️</div>
                <div>
                  <div className="text-xl font-bold">Shithead — OG Card Game</div>
                  <div className="text-sm text-gray-300">Play with friends online</div>
                </div>
              </div>
              <nav className="space-x-3">
                <a href="/" className="text-sm px-3 py-1 rounded hover:bg-white/5">Home</a>
                <a href="/lobby" className="text-sm px-3 py-1 rounded hover:bg-white/5">Lobby</a>
              </nav>
            </header>

            <main className="app-container flex-1 px-4 py-6">
              {children}
            </main>

            <footer className="text-center text-xs text-gray-400 py-3">Made for friends ✨</footer>
          </div>
        </UserProvider>
      </body>
    </html>
  );
}
