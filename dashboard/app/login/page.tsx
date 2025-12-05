"use client";

import { useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";
import { Lock, User } from "lucide-react";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      // 1. Send credentials to Backend
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        username,
        password,
      });

      // 2. Save the Token (The "Ticket")
      const token = response.data.token;
      localStorage.setItem("jwt_token", token);
      localStorage.setItem("username", response.data.username);

      // 3. Go to Dashboard
      router.push("/");
    } catch (err: any) {
      if (err.response?.status === 401) {
        // If 401, it means User Not Found. Let's try to register them automatically for this demo!
        try {
           await axios.post("http://localhost:8080/api/auth/register", { username, password });
           // If register works, try logging in again immediately
           const loginRes = await axios.post("http://localhost:8080/api/auth/login", { username, password });
           localStorage.setItem("jwt_token", loginRes.data.token);
           router.push("/");
           return;
        } catch (regErr) {
           setError("Login failed. Please try again.");
        }
      } else {
        setError("Invalid credentials or server error.");
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-xl shadow-md w-full max-w-md border border-gray-200">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-gray-900">Welcome Back</h1>
          <p className="text-gray-500 text-sm">Sign in to access the API Observability Platform</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm mb-4 border border-red-200">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
            <div className="relative">
              <User className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              <input
                type="text"
                required
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                placeholder="admin"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
              <input
                type="password"
                required
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                placeholder="••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg transition"
          >
            Sign In
          </button>
        </form>
      </div>
    </div>
  );
}