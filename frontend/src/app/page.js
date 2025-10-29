import Card from "@/components/Card";

export default function Home() {
  return (
    <main className="flex gap-4 p-8 bg-green-900 min-h-screen justify-center items-center">
      <Card suit="hearts" rank="ace" />
      <Card suit="spades" rank="10" />
      <Card suit="diamonds" rank="king" />
      <Card faceDown />
    </main>
  );
}
