import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter } from "react-router-dom";
import PhonePage from "./pages/PhonePage";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      <header className="border-b border-slate-800 bg-slate-900/60 backdrop-blur px-6 py-3 flex items-center gap-6">
        <h1 className="text-lg font-semibold tracking-tight">
          Number<span className="text-blue-500">Scope</span>
        </h1>
        <span className="text-xs text-slate-500">
          Análisis de metadatos telefónicos
        </span>
      </header>

      <main className="flex-1 p-6">{children}</main>

      <footer className="text-xs text-slate-500 px-6 py-3 border-t border-slate-800">
        Los datos mostrados provienen de información pública de bloques de
        numeración. No revela la ubicación del propietario.
      </footer>
    </div>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Layout>
          <PhonePage />
        </Layout>
      </BrowserRouter>
    </QueryClientProvider>
  );
}